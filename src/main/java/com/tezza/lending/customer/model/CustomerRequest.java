package com.tezza.lending.customer.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        LocalDate dateOfBirth,
        @DecimalMin("0.00") BigDecimal loanLimit,
        String riskGrade,
        String preferredNotificationChannel
) {
}
