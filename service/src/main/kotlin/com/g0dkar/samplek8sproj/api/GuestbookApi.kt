package com.g0dkar.samplek8sproj.api

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.persistence.GuestbookRepository
import kotlinx.coroutines.flow.flowOf
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/guestbook")
class GuestbookApi(private val guestbookRepository: GuestbookRepository) {
    @GetMapping("/test")
    suspend fun test() =
        flowOf(1..100)

    @GetMapping("/message/{id}")
    suspend fun getMessages(@PathVariable id: UUID): ResponseEntity<GuestbookMessage> =
        guestbookRepository.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}