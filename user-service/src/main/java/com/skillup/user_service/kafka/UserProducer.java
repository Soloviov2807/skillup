package com.skillup.user_service.kafka;

import com.skillup.user_service.model.dto.CoachProfileChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProducer {

    private final KafkaTemplate<String, CoachProfileChangedEvent> kafkaTemplate;

    public void send(Long id, String name, String avatarId){

        kafkaTemplate.send("coach-profile-changed", new CoachProfileChangedEvent(id, name, avatarId));
    }
}
