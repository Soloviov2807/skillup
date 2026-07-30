package com.example.course_service.kafka;

import com.example.common.event.CoursePurchasedEvent;
import com.example.common.event.CourseSaleCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CourseSaleProducer {

    private final KafkaTemplate<String, CourseSaleCompletedEvent> kafkaTemplate;

    public void send(Long coachId, BigDecimal amount) {

        kafkaTemplate.send(
                "course-sale-completed",
                new CourseSaleCompletedEvent(coachId, amount));




    }
}
