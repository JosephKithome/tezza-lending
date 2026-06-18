package com.tezza.lending.notification.model;

public record NotificationDeliveryRequest(
        NotificationChannel channel,
        String recipient,
        String subject,
        String message,
        boolean html
) {
}
