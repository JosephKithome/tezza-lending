package com.tezza.lending.notification.model;

public record RuleResponse(
        Long id,
        NotificationEventType eventType,
        String productCode,
        String customerSegment,
        NotificationChannel channel,
        boolean active
) {
    public static RuleResponse from(NotificationRule rule) {
        return new RuleResponse(
                rule.getId(),
                rule.getEventType(),
                rule.getProductCode(),
                rule.getCustomerSegment(),
                rule.getChannel(),
                rule.isActive());
    }
}
