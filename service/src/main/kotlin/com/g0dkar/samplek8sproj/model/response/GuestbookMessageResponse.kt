package com.g0dkar.samplek8sproj.model.response

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.VisitorType
import java.time.OffsetDateTime
import java.util.UUID

data class GuestbookMessageResponse(
    val id: UUID,
    val active: Boolean,
    val created: OffsetDateTime,
    val message: String,
    val visitorType: VisitorType,
    val children: List<GuestbookMessageResponse>
) {
    companion object {
        suspend fun of(
            guestbookMessage: GuestbookMessage,
            children: List<GuestbookMessageResponse> = listOf()
        ): GuestbookMessageResponse =
            GuestbookMessageResponse(
                id = guestbookMessage.id,
                active = guestbookMessage.active,
                created = guestbookMessage.created,
                message = guestbookMessage.message,
                visitorType = VisitorType.valueOf(guestbookMessage.visitorTypeId),
                children = children
            )
    }
}