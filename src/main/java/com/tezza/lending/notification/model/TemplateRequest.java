package com.tezza.lending.notification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TemplateRequest(
        @NotNull NotificationEventType eventType,
        @NotNull NotificationChannel channel,
        @NotBlank String subject,
        @NotBlank String body,
        boolean active
) {
}
