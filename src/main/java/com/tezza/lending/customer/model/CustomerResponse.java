package com.tezza.lending.customer.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        String customerNumber,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        BigDecimal loanLimit,
        String riskGrade,
        String preferredNotificationChannel,
        Instant registrationOtpExpiresAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getDateOfBirth(),
                customer.getLoanLimit(),
                customer.getRiskGrade(),
                customer.getPreferredNotificationChannel(),
                customer.getRegistrationOtpExpiresAt());
    }
}
