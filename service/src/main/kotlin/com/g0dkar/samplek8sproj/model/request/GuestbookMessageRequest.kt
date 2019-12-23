package com.g0dkar.samplek8sproj.model.request

import com.g0dkar.samplek8sproj.model.VisitorType
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class GuestbookMessageRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 1024)
    val message: String?,

    @field:NotNull
    val visitorType: VisitorType? = VisitorType.HUMAN,

    val parent: UUID? = null
)
