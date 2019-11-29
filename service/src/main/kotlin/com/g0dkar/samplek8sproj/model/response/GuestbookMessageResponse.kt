package com.g0dkar.samplek8sproj.model.response

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.VisitorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toCollection
import java.time.OffsetDateTime
import java.util.UUID

data class GuestbookMessageResponse(
    val id: UUID,
    val active: Boolean,
    val created: OffsetDateTime,
    val message: String,
    val visitorType: VisitorType,
    val children: List<GuestbookMessageResponse>?
) {
    companion object {
        suspend fun of(
            guestbookMessage: GuestbookMessage,
            children: Flow<GuestbookMessageResponse> = emptyFlow()
        ): GuestbookMessageResponse =
            GuestbookMessageResponse(
                id = guestbookMessage.id,
                active = guestbookMessage.active,
                created = guestbookMessage.created,
                message = guestbookMessage.message,
                visitorType = VisitorType.valueOf(guestbookMessage.visitorTypeId),
                children = children.toCollection(mutableListOf()).takeIf { it.isNotEmpty() }
            )
    }
}