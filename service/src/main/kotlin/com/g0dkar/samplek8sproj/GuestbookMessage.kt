package com.g0dkar.samplek8sproj

import java.time.OffsetDateTime
import java.util.*

data class GuestbookMessage(
        val id: UUID,
        val active: Boolean,
        val created: OffsetDateTime,
        val parent: UUID,
        val message: String,
        val visitorTypeId: Int
) {
    val visitorType: VisitorType
        get() = VisitorType.values()[visitorTypeId - 1]
}

enum class VisitorType {
    HUMAN,
    ORC,
    ELF,
    DWARF,
    UNDEAD,
    HALFLING;
}