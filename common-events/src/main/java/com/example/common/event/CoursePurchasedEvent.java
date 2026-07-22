package com.example.common.event;

import java.math.BigDecimal;

public record CoursePurchasedEvent(
        Long courseId,
        Long userId,
        BigDecimal price
) {
}
