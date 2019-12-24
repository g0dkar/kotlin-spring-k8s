package com.g0dkar.samplek8sproj.service

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import com.g0dkar.samplek8sproj.persistence.GuestbookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Validated
class GuestbookService(
    private val guestbookRepository: GuestbookRepository
) {
    fun findById(id: UUID): GuestbookMessageResponse? {
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

    fun getMessages(offset: Int = 0, max: Int = 50): List<GuestbookMessageResponse> =
        guestbookRepository.getMessages(offset, max)
            .map { GuestbookMessageResponse.of(it) }

    @Transactional(rollbackFor = [Throwable::class])
    fun create(request: GuestbookMessageRequest): GuestbookMessageResponse =
        GuestbookMessage(
            id = UUID.randomUUID(),
            active = true,
            created = OffsetDateTime.now(),
            message = request.message!!,
            visitorTypeId = request.visitorType!!.id,
            parent = request.parent
        ).also { guestbookRepository.save(it) }
            .let { GuestbookMessageResponse.of(it) }

    @Transactional(rollbackFor = [Throwable::class])
    fun delete(id: UUID): Boolean =
        guestbookRepository.setActive(id, false)
}
