package com.example.common.event;

public record CoachProfileChangedEvent(
        Long id,
        String name,
        String avatarId
) {
}
