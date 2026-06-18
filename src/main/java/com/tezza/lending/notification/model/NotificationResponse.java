package com.tezza.lending.notification.model;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long customerId,
        Long loanId,
        NotificationEventType eventType,
        NotificationChannel channel,
        NotificationStatus status,
        String recipient,
        String subject,
        String message,
        String deliveryDetail,
        Instant createdAt,
        Instant sentAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getCustomerId(),
                notification.getLoanId(),
                notification.getEventType(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getDeliveryDetail(),
                notification.getCreatedAt(),
                notification.getSentAt());
    }
}
