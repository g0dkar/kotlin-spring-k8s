package com.g0dkar.samplek8sproj.model

import java.time.OffsetDateTime
import java.util.UUID

data class GuestbookMessage(
    val id: UUID,
    val active: Boolean,
    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    val message: String,
    val visitorTypeId: Int,
    val parent: UUID?
)
