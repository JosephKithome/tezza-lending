package com.tezza.lending.notification.model;

import jakarta.validation.constraints.NotNull;

public record RuleRequest(
        @NotNull NotificationEventType eventType,
        String productCode,
        String customerSegment,
        @NotNull NotificationChannel channel,
        boolean active
) {
}
