package com.g0dkar.samplek8sproj.api

import com.g0dkar.samplek8sproj.IntegrationTest
import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.VisitorType
import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import com.g0dkar.samplek8sproj.persistence.jooq.Tables.MESSAGES
import com.g0dkar.samplek8sproj.util.randomFrom
import com.g0dkar.samplek8sproj.util.randomString
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import io.restassured.matcher.ResponseAwareMatcherComposer.and
import io.restassured.matcher.RestAssuredMatchers.endsWithPath
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import java.time.OffsetDateTime
import java.util.UUID

internal class GuestbookApiIntegrationTest(
    @Autowired val jooq: DSLContext
) : IntegrationTest() {

    companion object {
        private const val ENDPOINT: String = "/guestbook"
        private const val MESSAGE_TEXT: String = "Test Message"
        private val MESSAGE_VISITOR_TYPE = randomFrom(VisitorType.values())
    }

    @Test
    fun `GET should return a message given its id`() {
        val expected = testMessage()

        given()
            .`when`()
            .get("$ENDPOINT/${expected.first.id}")
            .then()
            .statusCode(OK.value())
            .body("id", equalTo(expected.first.id.toString()))
            .body("message", equalTo(MESSAGE_TEXT))
            .body("visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("active", equalTo(true))
    }

    @Test
    fun `GET should return 404 if a message doesn't exist`() {
        val nonExistingId = UUID.randomUUID()

        given()
            .`when`()
            .get("$ENDPOINT/$nonExistingId")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    fun `GET should return 404 if a message is inactive`() {
        val inactiveMessage = testMessage(active = false)

        given()
            .`when`()
            .get("$ENDPOINT/${inactiveMessage.first.id}")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    fun `DELETE should remove the message`() {
        val toBeDeleted = testMessage().first

        // Message exists
        given()
            .`when`()
            .get("$ENDPOINT/${toBeDeleted.id}")
            .then()
            .statusCode(OK.value())

        // Delete it
        given()
            .`when`()
            .delete("$ENDPOINT/${toBeDeleted.id}")
            .then()
            .statusCode(OK.value())

        // Message doesn't exist
        given()
            .`when`()
            .get("$ENDPOINT/${toBeDeleted.id}")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    fun `DELETE should return 204 if message doesn't exist`() {
        val nonExistingId = UUID.randomUUID()

        given()
            .`when`()
            .delete("$ENDPOINT/$nonExistingId")
            .then()
            .statusCode(NO_CONTENT.value())
    }

    @Test
    fun `DELETE should return 204 if message is already inactive`() {
        val inactiveMessage = testMessage(active = false)

        given()
            .`when`()
            .delete("$ENDPOINT/${inactiveMessage.first.id}")
            .then()
            .statusCode(OK.value())
    }

    @Test
    fun `POST should create message`() {
        val expectedContent = randomString()
        val expectedVisitorType = MESSAGE_VISITOR_TYPE.name
        val requestJson = "{\"message\":\"$expectedContent\",\"visitor_type\":\"$expectedVisitorType\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(CREATED.value())
            .contentType(JSON)
            .body("message", equalTo(expectedContent))
            .body("visitor_type", equalTo(expectedVisitorType))
            .body("active", equalTo(true))
            .header(
                "Location",
                and(
                    startsWith(ENDPOINT),
                    endsWithPath("id")
                )
            ).also {
                val createdLocation = it.extract().header("Location")

                given()
                    .`when`()
                    .get(createdLocation)
                    .then()
                    .statusCode(OK.value())
                    .body("message", equalTo(expectedContent))
                    .body("visitor_type", equalTo(expectedVisitorType))
                    .body("active", equalTo(true))
            }
    }

    @Test
    fun `POST should create message with a parent`() {
        val parentMessage = testMessage()
        val expectedContent = randomString()
        val expectedVisitorType = MESSAGE_VISITOR_TYPE.name
        val requestJson = "{\"message\":\"$expectedContent\",\"visitor_type\":\"$expectedVisitorType\",\"parent\":" +
            "\"${parentMessage.first.id}\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(CREATED.value())
            .contentType(JSON)
            .body("message", equalTo(expectedContent))
            .body("visitor_type", equalTo(expectedVisitorType))
            .body("active", equalTo(true))
            .body("parent", equalTo(parentMessage.first.id.toString()))
            .header(
                "Location",
                and(
                    startsWith(ENDPOINT),
                    endsWithPath("id")
                )
            )
    }

    @Test
    fun `POST should not create message with inactive parent`() {
        val parentMessage = testMessage(active = false)
        val expectedContent = randomString()
        val expectedVisitorType = MESSAGE_VISITOR_TYPE.name
        val requestJson = "{\"message\":\"$expectedContent\",\"visitor_type\":\"$expectedVisitorType\",\"parent\":" +
            "\"${parentMessage.first.id}\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
    }

    @Test
    fun `POST should not create message with non-existing parent`() {
        val parentId = UUID.randomUUID()
        val expectedContent = randomString()
        val expectedVisitorType = MESSAGE_VISITOR_TYPE.name
        val requestJson = "{\"message\":\"$expectedContent\",\"visitor_type\":\"$expectedVisitorType\",\"parent\":" +
            "\"$parentId\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
    }

    @Test
    fun `POST should validate request - no message`() {
        val expectedVisitorType = MESSAGE_VISITOR_TYPE.name
        val requestJson = "{\"visitor_type\":\"$expectedVisitorType\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
    }

    @Test
    fun `POST should validate request - no visitorType`() {
        val requestJson = "{\"message\":\"${randomString()}\"}"

        given()
            .`when`()
            .request()
            .contentType(JSON)
            .body(requestJson)
            .post(ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value())
    }

    @Test
    fun getAll() {
    }

    private fun testMessage(
        id: UUID = UUID.randomUUID(),
        parent: UUID? = null,
        content: String = MESSAGE_TEXT,
        visitorType: VisitorType = MESSAGE_VISITOR_TYPE,
        created: OffsetDateTime = OffsetDateTime.now(),
        updated: OffsetDateTime = OffsetDateTime.now(),
        active: Boolean = true,
        save: Boolean = true
    ): Triple<GuestbookMessage, GuestbookMessageRequest, GuestbookMessageResponse> {
        val message = GuestbookMessage(id, active, created, updated, content, visitorType.id, parent)

        val request = GuestbookMessageRequest(content, visitorType, parent)

        val response = runBlocking { GuestbookMessageResponse.of(message) }

        if (save) {
            jooq.insertInto(MESSAGES)
                .set(MESSAGES.ID, message.id)
                .set(MESSAGES.ACTIVE, message.active)
                .set(MESSAGES.CREATED, message.created)
                .set(MESSAGES.UPDATED, message.updated)
                .set(MESSAGES.MESSAGE, message.message)
                .set(MESSAGES.VISITOR_TYPE_ID, message.visitorTypeId)
                .set(MESSAGES.PARENT, message.parent)
                .execute()
        }

        return Triple(message, request, response)
    }
}
