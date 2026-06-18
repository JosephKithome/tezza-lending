package com.tezza.lending.notification.model;

public record TemplateResponse(
        Long id,
        NotificationEventType eventType,
        NotificationChannel channel,
        String subject,
        String body,
        boolean active
) {
    public static TemplateResponse from(NotificationTemplate template) {
        return new TemplateResponse(
                template.getId(),
                template.getEventType(),
                template.getChannel(),
                template.getSubject(),
                template.getBody(),
                template.isActive());
    }
}
