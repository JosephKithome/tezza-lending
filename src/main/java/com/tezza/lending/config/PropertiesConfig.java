package com.tezza.lending.config;

import com.tezza.lending.notification.model.GmailNotificationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GmailNotificationProperties.class)
public class PropertiesConfig {
}
