package com.g0dkar.samplek8sproj.model.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

internal class GuestbookMessageRequestTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()
    }

    @Test
    fun `message cannot be empty`() {
        val underTest = GuestbookMessageRequest(message = "")

        val result = validator.validate(underTest)

        assertNotNull(result)
        assertEquals(1, result.size)
    }

    @Test
    fun `message cannot be blank`() {
        val underTest = GuestbookMessageRequest(message = "          ")

        val result = validator.validate(underTest)

        assertNotNull(result)
        assertEquals(1, result.size)
    }

    @Test
    fun getVisitorType() {
    }

    @Test
    fun getParent() {
    }

    @Test
    fun testToString() {
    }
}