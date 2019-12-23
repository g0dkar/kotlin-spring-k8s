package com.g0dkar.sample.client.api

import com.g0dkar.sample.client.model.GuestbookMessage
import com.g0dkar.sample.client.model.GuestbookMessageRequest
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import retrofit2.Retrofit
import java.util.UUID
import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * Implements an API to interact with the Guestbook Service.
 */
@Component
@Validated
class GuestbookApi(
    retrofit: Retrofit,
    guestbookApiConfig: GuestbookApiConfig
) {
    companion object {
        private const val GUESTBOOK_API = "guestbookApi_R4J"
    }

    private val log: Logger = LogManager.getLogger(GuestbookApi::class.java)
    private val api: GuestbookApiDefinition = retrofit.create(GuestbookApiDefinition::class.java)

    init {
        log.debug("GuestbookApi initialized with config: $guestbookApiConfig")
    }

    /**
     * Returns a list of messages from the API. Accepts an offset and max parameters.
     */
    @Retry(name = GUESTBOOK_API)
    @Bulkhead(name = GUESTBOOK_API)
    @CircuitBreaker(name = GUESTBOOK_API)
    suspend fun getAll(offset: Int = 0, @Min(1) @Max(100) max: Int = 50): Flow<GuestbookMessage> =
        api.getAll(offset, max).asFlow()

    /**
     * Returns a single [GuestbookMessage] given its ID. If not found, `null` is returned.
     */
    @Retry(name = GUESTBOOK_API)
    @Bulkhead(name = GUESTBOOK_API)
    @CircuitBreaker(name = GUESTBOOK_API)
    suspend fun get(id: UUID): GuestbookMessage? =
        api.get(id)

    /**
     * Delets a [GuestbookMessage] given its ID.
     */
    @Retry(name = GUESTBOOK_API)
    @Bulkhead(name = GUESTBOOK_API)
    @CircuitBreaker(name = GUESTBOOK_API)
    suspend fun delete(id: UUID) {
        api.delete(id)
    }

    /**
     * Deletes a [GuestbookMessage].
     */
    @Retry(name = GUESTBOOK_API)
    @Bulkhead(name = GUESTBOOK_API)
    @CircuitBreaker(name = GUESTBOOK_API)
    suspend fun delete(guestbookMessage: GuestbookMessage) =
        delete(guestbookMessage.id)

    /**
     * Creates a new [GuestbookMessage].
     */
    @Retry(name = GUESTBOOK_API)
    @Bulkhead(name = GUESTBOOK_API)
    @CircuitBreaker(name = GUESTBOOK_API)
    suspend fun create(message: GuestbookMessageRequest): GuestbookMessage =
        api.create(message)
}
