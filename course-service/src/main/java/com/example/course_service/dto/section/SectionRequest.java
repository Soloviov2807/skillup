package com.example.course_service.dto.section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SectionRequest(

        @Size(min = 5, max = 30, message = "Section name must be 5-30 characters")
        @NotBlank(message = "Section name is required")
        String name

) {
}
