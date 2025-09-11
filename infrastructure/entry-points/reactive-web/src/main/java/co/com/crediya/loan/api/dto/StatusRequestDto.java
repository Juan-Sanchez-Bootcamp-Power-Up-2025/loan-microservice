package co.com.crediya.loan.api.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusRequestDto(
        @NotBlank(message = "status is required")
        String status
){}


