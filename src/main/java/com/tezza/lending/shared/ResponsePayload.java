package com.tezza.lending.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public record ResponsePayload(
        Header header,
        @JsonInclude(JsonInclude.Include.ALWAYS) Object body
) {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public static ResponsePayload ok(String requestRefId, String message, Object body) {
        return of(requestRefId, message, 200, message, body);
    }

    public static ResponsePayload created(String requestRefId, String message, Object body) {
        return of(requestRefId, message, 201, message, body);
    }

    public static ResponsePayload error(String requestRefId, String message, int code, Object body) {
        return of(requestRefId, message, code, message, body);
    }

    public static ResponsePayload of(
            String requestRefId,
            String responseMessage,
            int responseCode,
            String customerMessage,
            Object body
    ) {
        return new ResponsePayload(
                new Header(
                        requestRefId,
                        responseMessage,
                        responseCode,
                        customerMessage,
                        OffsetDateTime.now(ZoneOffset.UTC).format(TIMESTAMP_FORMATTER)
                ),
                body == null ? Map.of() : body
        );
    }

    public record Header(
            String requestRefId,
            String responseMessage,
            int responseCode,
            String customerMessage,
            String timestamp
    ) {
    }
}
