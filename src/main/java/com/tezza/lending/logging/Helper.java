package com.tezza.lending.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tezza.lending.shared.RequestContext;
import com.tezza.lending.shared.ResponsePayload;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Component
public class Helper {
    private static final String SOURCE_SYSTEM = "TEZZA-LENDING-API";
    private static final String TARGET_SYSTEM = "LENDING-CORE";
    private static final int MAX_PAYLOAD_LENGTH = 4000;
    private static final DateTimeFormatter REQUEST_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private static final ObjectMapper FALLBACK_MAPPER = JsonMapper.builder().findAndAddModules().build();

    private static ObjectMapper mapper;

    public Helper(ObjectMapper mapper) {
        Helper.mapper = mapper;
    }

    public static ResponseEntity<ResponsePayload> tooManyRequests() {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponsePayload.error(
                        RequestContext.requestId(),
                        "Rate limit exceeded",
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        null));
    }

    public static String objectToString(Object value) {
        return payload(value);
    }

    // Author: Joseph Kithome | Date: 18/Jun/2026 | Global inline API exchange logger helper.
    public static void logger(
            Logger log,
            String method,
            String path,
            int status,
            Object requestPayload,
            Object responsePayload) {
        String requestId = RequestContext.requestId() == null ? UUID.randomUUID().toString() : RequestContext.requestId();
        String requestTime = RequestContext.requestTime() == null
                ? OffsetDateTime.now(ZoneOffset.UTC).format(REQUEST_TIME_FORMATTER)
                : RequestContext.requestTime();

        log.info("SourceSystem ={} || TargetSystem={} || RequestId={} || RequestTime={} || RequestPayload={} || ResponsePayload={} || Method={} || Path={} || Status={}",
                SOURCE_SYSTEM,
                TARGET_SYSTEM,
                requestId,
                requestTime,
                payload(requestPayload),
                payload(responsePayload),
                method,
                path,
                status);
    }

    private static String payload(Object value) {
        if (value == null) {
            return "{}";
        }
        try {
            ObjectMapper activeMapper = mapper == null ? FALLBACK_MAPPER : mapper;
            return normalize(activeMapper.writeValueAsString(value));
        } catch (JsonProcessingException ex) {
            return normalize(String.valueOf(value));
        }
    }

    private static String normalize(String value) {
        String normalized = value.replaceAll("\\s+", " ").trim();
        return normalized.length() > MAX_PAYLOAD_LENGTH
                ? normalized.substring(0, MAX_PAYLOAD_LENGTH) + "...[truncated]"
                : normalized;
    }
}
