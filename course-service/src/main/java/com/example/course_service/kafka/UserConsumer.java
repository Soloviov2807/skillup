package com.example.course_service.kafka;


import com.example.common.event.CoachProfileChangedEvent;
import com.example.course_service.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final CourseService courseService;


    @KafkaListener(topics = "coach-profile-changed")
    public void listen(CoachProfileChangedEvent coachProfileChangedEvent){

        System.out.println("EVENT: " + coachProfileChangedEvent);

        courseService.handleCoachProfileChanged(coachProfileChangedEvent);

    }


}
