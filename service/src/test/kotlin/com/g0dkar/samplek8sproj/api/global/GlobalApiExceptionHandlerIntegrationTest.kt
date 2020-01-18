package com.g0dkar.samplek8sproj.api.global

import com.g0dkar.samplek8sproj.IntegrationTest
import com.g0dkar.samplek8sproj.model.VisitorType
import com.g0dkar.samplek8sproj.util.randomFrom
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST

internal class GlobalApiExceptionHandlerIntegrationTest : IntegrationTest() {

    companion object {
        private const val ENDPOINT: String = "/guestbook"
        private const val MESSAGE_TEXT: String = "Test Message"
        private val MESSAGE_VISITOR_TYPE = randomFrom(VisitorType.values())
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
            .body("message", notNullValue())
            .body("code", equalTo(BAD_REQUEST.reasonPhrase))
            .body("timestamp", notNullValue())
            .body(
                "status_info",
                equalTo("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/${BAD_REQUEST.value()}")
            )
    }

    @Test
    fun testHandle() {
    }

    @Test
    fun testHandle1() {
    }
}
