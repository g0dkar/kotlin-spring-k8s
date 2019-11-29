package com.g0dkar.samplek8sproj.service

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.persistence.GuestbookRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class GuestbookService(
    private val guestbookRepository: GuestbookRepository
) {
    suspend fun findById(id: UUID): GuestbookMessage? =
        guestbookRepository.findById(id)

    suspend fun getMessages(offset: Int = 0, max: Int = 50): Flow<GuestbookMessage> =
        guestbookRepository.getMessages(offset, max)

    suspend fun create(request: GuestbookMessageRequest): GuestbookMessage =
        GuestbookMessage(
            id = UUID.randomUUID(),
            active = true,
            created = OffsetDateTime.now(),
            message = request.message,
            visitorTypeId = request.visitorType.id,
            parent = request.parent
        ).also { guestbookRepository.save(it) }

    suspend fun delete(id: UUID): Boolean =
        guestbookRepository.setActive(id, false)
}
