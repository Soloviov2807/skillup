package com.skillup.user_service.model.dto;

public record CoachProfileChangedEvent(
        Long id,
        String name,
        String avatarId
) {
}
