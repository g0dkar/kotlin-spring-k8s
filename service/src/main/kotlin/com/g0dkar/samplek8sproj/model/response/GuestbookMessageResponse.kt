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
    val parent: UUID?,
    val children: List<GuestbookMessageResponse>?
) {
    companion object {
        /**
         * Converts a [GuestbookMessage] into a [GuestbookMessageResponse]. The [children] argument will be used for the
         * response. It should be filled by the caller.
         */
        fun of(
            guestbookMessage: GuestbookMessage,
            children: List<GuestbookMessageResponse> = listOf()
        ): GuestbookMessageResponse =
            GuestbookMessageResponse(
                id = guestbookMessage.id,
                active = guestbookMessage.active,
                created = guestbookMessage.created,
                message = guestbookMessage.message,
                visitorType = VisitorType.valueOf(guestbookMessage.visitorTypeId),
                parent = guestbookMessage.parent,
                children = children
                // children = children.toCollection(mutableListOf()).takeIf { it.isNotEmpty() }
            )
    }
}
