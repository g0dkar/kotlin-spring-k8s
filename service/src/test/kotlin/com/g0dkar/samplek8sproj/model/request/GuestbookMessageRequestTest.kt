package com.g0dkar.samplek8sproj.model.request

import com.g0dkar.samplek8sproj.model.VisitorType
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.LinkedList
import javax.validation.Validation

internal class GuestbookMessageRequestTest {

    companion object {
        private const val VALID_MESSAGE = "Test"
        private val VALID_VISITOR_TYPE = VisitorType.HUMAN
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `GuestbookMessageRequest should be a data class`() {
        val underTest = GuestbookMessageRequest::class

        assertTrue(underTest.isData)
    }

    @Test
    fun `message cannot be empty`() {
        val underTest = GuestbookMessageRequest(message = "", visitorType = VALID_VISITOR_TYPE)

        val result = assertDoesNotThrow { validator.validate(underTest) }

        assertNotNull(result)
        assertEquals(2, result.size)

        val violations = LinkedList(result)

        violations.sortBy { it.messageTemplate }

        assertEquals("message", violations[0].propertyPath.toString())
        assertEquals("validation_notBlank", violations[0].message)

        assertEquals("message", violations[1].propertyPath.toString())
        assertEquals("validation_size", violations[1].message)
    }

    @Test
    fun `message cannot be blank`() {
        val underTest = GuestbookMessageRequest(message = "          ", visitorType = VALID_VISITOR_TYPE)

        val result = assertDoesNotThrow { validator.validate(underTest) }

        assertNotNull(result)
        assertEquals(1, result.size)

        val constraintViolation = result.iterator().next()
        assertEquals("message", constraintViolation.propertyPath.toString())
        assertEquals("validation_notBlank", constraintViolation.message)
    }

    @Test
    fun `message cannot be over 1024 chars`() {
        val longString = RandomStringUtils.random(1025)
        val underTest = GuestbookMessageRequest(message = longString, visitorType = VALID_VISITOR_TYPE)

        val result = assertDoesNotThrow { validator.validate(underTest) }

        assertNotNull(result)
        assertEquals(1, result.size)

        val constraintViolation = result.iterator().next()
        assertEquals("message", constraintViolation.propertyPath.toString())
        assertEquals("validation_size", constraintViolation.message)
    }

    @Test
    fun `visitor type cannot be null`() {
        val underTest = GuestbookMessageRequest(
            message = VALID_MESSAGE,
            visitorType = null
        )

        val result = assertDoesNotThrow { validator.validate(underTest) }

        assertNotNull(result)
        assertEquals(1, result.size)

        val constraintViolation = result.iterator().next()
        assertEquals("visitorType", constraintViolation.propertyPath.toString())
        assertEquals("validation_notNull", constraintViolation.message)
    }

    @Test
    fun `valid object should not have violations`() {
        val underTest = GuestbookMessageRequest(
            message = VALID_MESSAGE,
            visitorType = VALID_VISITOR_TYPE
        )

        val result = assertDoesNotThrow { validator.validate(underTest) }

        assertNotNull(result)
        assertEquals(0, result.size)
    }
}
