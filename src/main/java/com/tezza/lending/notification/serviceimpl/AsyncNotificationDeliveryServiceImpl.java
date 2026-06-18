package com.tezza.lending.notification.serviceimpl;

import com.tezza.lending.logging.Helper;
import com.tezza.lending.notification.model.Notification;
import com.tezza.lending.notification.model.NotificationDeliveryRequest;
import com.tezza.lending.notification.model.NotificationDeliveryResult;
import com.tezza.lending.notification.model.NotificationStatus;
import com.tezza.lending.notification.repository.NotificationRepository;
import com.tezza.lending.notification.service.AsyncNotificationDeliveryService;
import com.tezza.lending.notification.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsyncNotificationDeliveryServiceImpl implements AsyncNotificationDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(AsyncNotificationDeliveryServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> senders;

    public AsyncNotificationDeliveryServiceImpl(NotificationRepository notificationRepository,
                                                List<NotificationSender> senders) {
        this.notificationRepository = notificationRepository;
        this.senders = senders;
    }

    @Async
    @Override
    @Transactional
    public void deliver(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            NotificationDeliveryResult delivery = send(notification);
            notification.setStatus(delivery.delivered() ? NotificationStatus.SENT : NotificationStatus.FAILED);
            notification.setSentAt(delivery.delivered() ? Instant.now() : null);
            notification.setDeliveryDetail(delivery.detail());
            notificationRepository.save(notification);

            Helper.logger(
                    log,
                    "ASYNC",
                    "/notifications/delivery/" + notification.getId(),
                    delivery.delivered() ? 200 : 500,
                    requestPayload(notification),
                    responsePayload(notification, delivery));
        });
    }

    private NotificationDeliveryResult send(Notification notification) {
        NotificationDeliveryRequest request = new NotificationDeliveryRequest(
                notification.getChannel(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                isHtml(notification.getMessage()));
        return senders.stream()
                .filter(sender -> sender.supports(notification.getChannel()))
                .findFirst()
                .map(sender -> sender.send(request))
                .orElseGet(() -> NotificationDeliveryResult.failed("No notification sender found for " + notification.getChannel()));
    }

    private boolean isHtml(String message) {
        return message != null && message.stripLeading().startsWith("<");
    }

    private Map<String, Object> requestPayload(Notification notification) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("notificationId", notification.getId());
        payload.put("eventType", notification.getEventType());
        payload.put("channel", notification.getChannel());
        payload.put("recipient", notification.getRecipient());
        payload.put("subject", notification.getSubject());
        return payload;
    }

    private Map<String, Object> responsePayload(Notification notification, NotificationDeliveryResult delivery) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("notificationId", notification.getId());
        payload.put("delivered", delivery.delivered());
        payload.put("detail", delivery.detail());
        payload.put("status", notification.getStatus());
        return payload;
    }
}
