package com.tezza.lending.notification.repository;

import com.tezza.lending.notification.model.NotificationChannel;
import com.tezza.lending.notification.model.NotificationEventType;
import com.tezza.lending.notification.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findTopByEventTypeAndChannelAndActiveTrueOrderByIdDesc(
            NotificationEventType eventType,
            NotificationChannel channel);
}
