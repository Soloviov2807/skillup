package com.example.common.event;

import java.math.BigDecimal;

public record CourseSaleCompletedEvent(
        long coachId,
        BigDecimal amount
) {
}
