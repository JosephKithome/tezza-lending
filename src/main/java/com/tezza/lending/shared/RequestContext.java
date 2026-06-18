package com.tezza.lending.shared;

import org.slf4j.MDC;

public final class RequestContext {
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_TIME = "requestTime";

    private RequestContext() {
    }

    public static String requestId() {
        return MDC.get(REQUEST_ID);
    }

    public static String requestTime() {
        return MDC.get(REQUEST_TIME);
    }
}
