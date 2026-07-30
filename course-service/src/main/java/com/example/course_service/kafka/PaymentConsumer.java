package com.example.course_service.kafka;



import com.example.common.event.CoursePurchasedEvent;
import com.example.course_service.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final EnrollmentService enrollmentService;


    @KafkaListener(topics = "course-purchased")
    public void listen(CoursePurchasedEvent event) {

        enrollmentService.enroll(event.courseId(), event.userId(), event.price());


    }
}
