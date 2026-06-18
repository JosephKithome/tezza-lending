package com.tezza.lending.notification.model;

public record NotificationDeliveryResult(boolean delivered, String detail) {
    public static NotificationDeliveryResult delivered(String detail) {
        return new NotificationDeliveryResult(true, detail);
    }

    public static NotificationDeliveryResult failed(String detail) {
        return new NotificationDeliveryResult(false, detail);
    }
}
