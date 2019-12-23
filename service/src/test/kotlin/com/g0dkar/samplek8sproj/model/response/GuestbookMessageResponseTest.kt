package com.g0dkar.samplek8sproj.model.response

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.VisitorType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.OffsetDateTime
import java.util.UUID

internal class GuestbookMessageResponseTest {

    companion object {
        private val ID = UUID.randomUUID()
        private val PARENT = UUID.randomUUID()
        private const val MESSAGE = "Test"
        private const val ACTIVE = true
        private val CREATED = OffsetDateTime.now()
        private val VISITOR_TYPE = VisitorType.HUMAN
    }

    @Test
    fun `GuestbookMessageResponse should be a data class`() {
        val underTest = GuestbookMessageResponse::class

        assertTrue(underTest.isData)
    }

    @Test
    fun `should convert from GuestbookMessage`() {
        val expected = GuestbookMessage(
            id = ID,
            message = MESSAGE,
            active = ACTIVE,
            created = CREATED,
            parent = PARENT,
            visitorTypeId = VISITOR_TYPE.id
        )

        val result = assertDoesNotThrow { runBlocking { GuestbookMessageResponse.of(expected) } }

        assertNotNull(result)
    }
}
