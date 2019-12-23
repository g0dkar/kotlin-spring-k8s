package com.g0dkar.sample.client.model

import java.time.OffsetDateTime
import java.util.UUID

/**
 * A Guestbook Message. If this message has a thread, its [children] will be populated by the responses.
 *
 * @param id Guestbook message ID
 * @param created When was this message created
 * @param message The message content
 * @param visitorType The type of visitor that left the message
 * @param children The responses to this message
 */
data class GuestbookMessage(
    val id: UUID,
    val created: OffsetDateTime,
    val message: String,
    val visitorType: VisitorType,
    val children: List<GuestbookMessage>?
)
