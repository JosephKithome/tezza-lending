package com.tezza.lending.notification.service;

import com.tezza.lending.notification.model.*;


public interface NotificationSender {
    boolean supports(NotificationChannel channel);

    NotificationDeliveryResult send(NotificationDeliveryRequest request);
}
