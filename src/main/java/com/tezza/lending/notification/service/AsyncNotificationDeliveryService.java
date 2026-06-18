package com.tezza.lending.notification.service;

import com.tezza.lending.notification.model.*;


public interface AsyncNotificationDeliveryService {
    void deliver(Long notificationId);
}
