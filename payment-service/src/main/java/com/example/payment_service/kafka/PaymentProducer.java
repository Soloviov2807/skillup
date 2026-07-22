package com.example.payment_service.kafka;


import com.example.common.event.CoursePurchasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, CoursePurchasedEvent> kafkaTemplate;

    public void send(Long courseId, Long userId, BigDecimal amount) {

        kafkaTemplate.send(
                "course-purchased",
                new CoursePurchasedEvent(courseId, userId, amount));




    }
}
