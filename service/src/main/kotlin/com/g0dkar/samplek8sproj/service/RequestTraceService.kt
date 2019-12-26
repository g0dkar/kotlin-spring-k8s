package com.g0dkar.samplek8sproj.service

import datadog.trace.api.CorrelationIdentifier
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.RequestScope
import java.util.UUID

/**
 * This service generates a unique ID for each request.
 */
@Service
@RequestScope
class RequestTraceService {
    val requestIdKey = "request_id"
    val requestId = UUID.randomUUID().toString()

    val traceIdKey = CorrelationIdentifier.getTraceIdKey()
    val traceId: String?
        get() = CorrelationIdentifier.getTraceId()?.takeIf { it != "0" }

    val spanIdKey = CorrelationIdentifier.getSpanIdKey()
    val spanId: String?
        get() = CorrelationIdentifier.getSpanId()?.takeIf { it != "0" }
}
