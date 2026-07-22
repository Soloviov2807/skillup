package com.skillup.user_service.kafka;

import com.example.common.event.CoachProfileChangedEvent;
import com.example.common.event.CourseSaleCompletedEvent;
import com.skillup.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseSaleConsumer {

    private final UserService service;


    @KafkaListener(topics = "course-sale-completed", groupId = "user-service")
    public void listen(CourseSaleCompletedEvent courseSaleCompletedEvent){

        System.out.println("EVENT: " + courseSaleCompletedEvent);

        service.increaseBalance(courseSaleCompletedEvent);

    }


}