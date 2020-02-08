package com.g0dkar.samplek8sproj.api.global

import com.g0dkar.samplek8sproj.IntegrationTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus.BAD_REQUEST

internal class GlobalApiExceptionHandlerIntegrationTest : IntegrationTest() {

    companion object {
        private const val ENDPOINT = "/guestbook"
        private const val STATUS_CODE_PREFIX = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status"
    }

    @Test
    fun `test HttpMessageNotReadableException - invalid JSON`() {
        val invalidJson = "{\"incomplete\":\"json\""

        given()
            .`when`()
            .contentType(JSON)
            .body(invalidJson)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
            .body("status", equalTo(BAD_REQUEST.value()))
            .body("message", equalTo(DecodingException::class.java.simpleName))
            .body("code", equalTo(BAD_REQUEST.name))
            .body("timestamp", notNullValue())
            .body("status_info", equalTo("$STATUS_CODE_PREFIX/${BAD_REQUEST.value()}"))
    }

    @Test
    fun `test MethodArgumentNotValidException - validation error`() {
        val invalidMessage = "{\"message\":\"Message only\",\"visitor_type\":null}"

        given()
            .`when`()
            .contentType(JSON)
            .body(invalidMessage)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
            .body("status", equalTo(BAD_REQUEST.value()))
            .body("message", equalTo(BAD_REQUEST.reasonPhrase))
            .body("code", equalTo(BAD_REQUEST.name))
            .body("timestamp", notNullValue())
            .body("status_info", equalTo("$STATUS_CODE_PREFIX/${BAD_REQUEST.value()}"))
    }
}
