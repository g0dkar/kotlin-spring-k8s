package com.g0dkar.samplek8sproj.model.response

import datadog.trace.api.CorrelationIdentifier
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

data class ApiError(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    val error: String = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
    val code: String? = null,
    val tracing: ApiErrorTracing = ApiErrorTracing()
)

data class ApiErrorTracing(
    val traceId: String = CorrelationIdentifier.getTraceId(),
    val spanId: String = CorrelationIdentifier.getSpanId()
)
