package com.tezza.lending.notification.repository;

import com.tezza.lending.notification.model.*;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long> {
    Optional<NotificationRule> findFirstByEventTypeAndProductCodeAndCustomerSegmentAndActiveTrue(
            NotificationEventType eventType,
            String productCode,
            String customerSegment);
}
