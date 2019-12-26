package com.g0dkar.samplek8sproj.api.open

import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import com.g0dkar.samplek8sproj.service.GuestbookService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/guestbook")
class GuestbookApi(
    private val guestbookService: GuestbookService
) {
    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: UUID): ResponseEntity<GuestbookMessageResponse> =
        guestbookService.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<Any> =
        if (guestbookService.delete(id)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }

    @PostMapping
    suspend fun create(@Valid @RequestBody message: GuestbookMessageRequest): ResponseEntity<GuestbookMessageResponse> =
        guestbookService.create(message)
            .let {
                val createdPath = UriComponentsBuilder
                    .fromPath("/guestbook/{id}")
                    .buildAndExpand(it.id)

                ResponseEntity
                    .created(createdPath.toUri())
                    .body(it)
            }

    @GetMapping
    suspend fun getAll(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "50") max: Int
    ): Flow<GuestbookMessageResponse> =
        guestbookService.getMessages(offset, max).asFlow()
}
