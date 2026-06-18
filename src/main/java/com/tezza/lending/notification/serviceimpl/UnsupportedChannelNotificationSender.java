package com.tezza.lending.notification.serviceimpl;

import com.tezza.lending.customer.model.*;
import com.tezza.lending.loan.model.*;
import com.tezza.lending.notification.model.*;
import com.tezza.lending.notification.repository.*;
import com.tezza.lending.notification.service.*;


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class UnsupportedChannelNotificationSender implements NotificationSender {
    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS || channel == NotificationChannel.PUSH;
    }

    @Override
    public NotificationDeliveryResult send(NotificationDeliveryRequest request) {
        return NotificationDeliveryResult.failed(request.channel() + " delivery provider is not configured.");
    }
}
