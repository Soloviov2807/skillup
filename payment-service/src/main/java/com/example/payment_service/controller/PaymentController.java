package com.example.payment_service.controller;

import com.example.payment_service.dto.CheckoutResponse;
import com.example.payment_service.model.JwtUserPrincipal;
import com.example.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;



    @PostMapping("/checkout/{courseId}")
    public ResponseEntity<CheckoutResponse> createCheckout(@PathVariable long courseId, @AuthenticationPrincipal JwtUserPrincipal principal) throws StripeException {
        return ResponseEntity.ok(stripeService.createCheckout(courseId, principal.userId()));
    }


    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature){

        stripeService.handleWebhook(payload, signature);

        return ResponseEntity.ok().build();
    }

}
