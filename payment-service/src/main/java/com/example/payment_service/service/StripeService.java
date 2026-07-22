package com.example.payment_service.service;

import com.example.payment_service.config.StripeProperties;
import com.example.payment_service.controller.CourseClient;
import com.example.payment_service.dto.CheckoutResponse;
import com.example.payment_service.dto.CoursePaymentInfoResponse;
import com.example.payment_service.kafka.PaymentProducer;
import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentStatus;
import com.example.payment_service.repo.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class StripeService {

    private final CourseClient courseClient;
    private final PaymentRepository paymentRepository;
    private final StripeProperties stripeProperties;
    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper;


    private String getAuthHeader(){
        String token = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials();

        return  "Bearer " + token;
    }

    public CheckoutResponse createCheckout(long courseId, long userId) throws StripeException {



        if(courseClient.isPurchased(courseId, getAuthHeader()).getBody()){

            throw new RuntimeException("Course already purchased");
        }

        ResponseEntity<CoursePaymentInfoResponse> response = courseClient.getCoursePaymentInfo(courseId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new RuntimeException("Failed to get course payment info");
        }

        CoursePaymentInfoResponse course = response.getBody();

        long amount = course.price().multiply(BigDecimal.valueOf(100)).longValue();

        Payment payment = new Payment();

        payment.setAmount(course.price());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCourseId(course.courseId());
        payment.setUserId(userId);

        payment = paymentRepository.save(payment);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("paymentId", payment.getId().toString())
                .putMetadata("courseId", String.valueOf(courseId))
                .putMetadata("userId", String.valueOf(userId))
                .setSuccessUrl(
                        "http://localhost:5173/payment-success"
                )
                .setCancelUrl(
                        "http://localhost:5173/payment-cancel"
                )
                .addLineItem(
                        SessionCreateParams.LineItem
                                .builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(
                                                        "usd"
                                                )
                                                .setUnitAmount(amount)
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder()
                                                        .setName(course.courseName())
                                                        .build())
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);


        payment.setStripeSessionId(session.getId());


        paymentRepository.save(payment);



        return new CheckoutResponse(session.getUrl());




    }

    public void handleWebhook(String payload, String signature) {


        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    stripeProperties.getWebhookSecret()
            );


        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        try {


            JsonNode root = objectMapper.readTree(payload);

            String sessionId = root
                    .path("data")
                    .path("object")
                    .path("id")
                    .asText();


            Session session = Session.retrieve(sessionId);


            String paymentIdStr = session.getMetadata().get("paymentId");


            Long paymentId = Long.parseLong(paymentIdStr);


            Payment payment = paymentRepository
                    .findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));


            payment.setStatus(PaymentStatus.SUCCESS);

            paymentRepository.save(payment);


            paymentProducer.send(
                    payment.getCourseId(),
                    payment.getUserId(),
                    payment.getAmount()
            );


        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
