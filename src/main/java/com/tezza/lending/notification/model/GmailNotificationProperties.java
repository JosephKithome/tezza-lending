package com.tezza.lending.notification.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.notifications.email")
public record GmailNotificationProperties(boolean enabled, String from) {
}
