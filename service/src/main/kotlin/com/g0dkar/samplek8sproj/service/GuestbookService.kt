package com.g0dkar.samplek8sproj.service

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import com.g0dkar.samplek8sproj.persistence.GuestbookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class GuestbookService(
    private val guestbookRepository: GuestbookRepository
) {
    suspend fun findById(id: UUID): GuestbookMessageResponse? {
        val guestbookMessage = guestbookRepository.findById(id)

        return if (guestbookMessage != null) {
            GuestbookMessageResponse.of(
                guestbookMessage,
                guestbookRepository.findByParent(guestbookMessage.id)
                    .map { findById(it.id)!! }
            )
        } else {
            null
        }
    }

    suspend fun getMessages(offset: Int = 0, max: Int = 50): Flow<GuestbookMessageResponse> =
        guestbookRepository.getMessages(offset, max)
            .map { GuestbookMessageResponse.of(it) }

    suspend fun create(request: GuestbookMessageRequest): GuestbookMessageResponse =
        GuestbookMessage(
            id = UUID.randomUUID(),
            active = true,
            created = OffsetDateTime.now(),
            message = request.message!!,
            visitorTypeId = request.visitorType!!.id,
            parent = request.parent
        ).also { guestbookRepository.save(it) }
            .let { GuestbookMessageResponse.of(it) }

    suspend fun delete(id: UUID): Boolean =
        guestbookRepository.setActive(id, false)
}
