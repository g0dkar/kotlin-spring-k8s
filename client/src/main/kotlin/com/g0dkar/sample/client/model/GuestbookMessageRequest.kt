package com.g0dkar.sample.client.model

import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Used to create Guestbook message.
 *
 * @param message The Guestbook message
 * @param visitorType The type of Visitor (default = [VisitorType.HUMAN])
 * @param parent ID of the Parent message (if part of a thread)
 */
data class GuestbookMessageRequest(
    @NotBlank
    @Size(min = 1, max = 1024)
    val message: String,

    @NotNull
    val visitorType: VisitorType = VisitorType.HUMAN,

    val parent: UUID?
)
