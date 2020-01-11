package com.g0dkar.samplek8sproj.api

import com.g0dkar.samplek8sproj.IntegrationTest
import com.g0dkar.samplek8sproj.extensions.log
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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.CoreMatchers.startsWith
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
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

    @AfterEach
    fun deleteAllMessages() {
        val deletedMessages = jooq.delete(MESSAGES).execute()
        log.debug("[After Test] Deleted $deletedMessages messages.")
    }

    @Test
    fun `GET by Id should return a message given its id`() {
        val expected = createTestMessage().first

        given()
            .`when`()
            .get("$ENDPOINT/${expected.id}")
            .then()
            .statusCode(OK.value())
            .body("id", equalTo(expected.id.toString()))
            .body("message", equalTo(MESSAGE_TEXT))
            .body("visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("active", equalTo(true))
            .body("parent", nullValue())
    }

    @Test
    fun `GET by Id should return the message children`() {
        val parentMessage = createTestMessage().first

        val child1 = createTestMessage(parent = parentMessage.id, content = randomString()).first
        val child2 = createTestMessage(parent = parentMessage.id, content = randomString()).first
        val child3 = createTestMessage(parent = parentMessage.id, content = randomString()).first

        given()
            .`when`()
            .get("$ENDPOINT/${parentMessage.id}")
            .then()
            .statusCode(OK.value())
            .body("id", equalTo(parentMessage.id.toString()))
            .body("message", equalTo(MESSAGE_TEXT))
            .body("visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("active", equalTo(true))
            .body("parent", nullValue())
            .body("children.size()", `is`(3))
            // Child 1
            .body("children[0].id", equalTo(child1.id.toString()))
            .body("children[0].message", equalTo(child1.message))
            .body("children[0].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("children[0].active", equalTo(true))
            .body("children[0].parent", equalTo(parentMessage.id.toString()))
            // Child 2
            .body("children[1].id", equalTo(child2.id.toString()))
            .body("children[1].message", equalTo(child2.message))
            .body("children[1].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("children[1].active", equalTo(true))
            .body("children[1].parent", equalTo(parentMessage.id.toString()))
            // Child 3
            .body("children[2].id", equalTo(child3.id.toString()))
            .body("children[2].message", equalTo(child3.message))
            .body("children[2].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("children[2].active", equalTo(true))
            .body("children[2].parent", equalTo(parentMessage.id.toString()))
    }

    @Test
    fun `GET by Id should return 404 if a message doesn't exist`() {
        val nonExistingId = UUID.randomUUID()

        given()
            .`when`()
            .get("$ENDPOINT/$nonExistingId")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    fun `GET by Id should return 404 if a message is inactive`() {
        val inactiveMessage = createTestMessage(active = false)

        given()
            .`when`()
            .get("$ENDPOINT/${inactiveMessage.first.id}")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    fun `DELETE should remove the message`() {
        val toBeDeleted = createTestMessage().first

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
        val inactiveMessage = createTestMessage(active = false)

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
        val parentMessage = createTestMessage()
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
        val parentMessage = createTestMessage(active = false)
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
    fun `GET all should return a list of messages without parent by DESC time`() {
        val oldestMessage = createTestMessage(
            content = randomString(),
            created = OffsetDateTime.now().minusDays(2)
        ).first
        val oldMessage = createTestMessage(
            content = randomString(),
            created = OffsetDateTime.now().minusDays(1)
        ).first
        val newestMessage = createTestMessage(content = randomString()).first

        val child = createTestMessage(parent = newestMessage.id)

        val expected = listOf(
            newestMessage,
            oldMessage,
            oldestMessage
        )

        given()
            .`when`()
            .get(ENDPOINT)
            .then()
            .statusCode(OK.value())
            .body("size()", `is`(3))
            // Newest message
            .body("[0].id", equalTo(expected[0].id.toString()))
            .body("[0].message", equalTo(expected[0].message))
            .body("[0].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("[0].active", equalTo(true))
            .body("[0].parent", nullValue())
            // Old message
            .body("[1].id", equalTo(expected[1].id.toString()))
            .body("[1].message", equalTo(expected[1].message))
            .body("[1].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("[1].active", equalTo(true))
            .body("[1].parent", nullValue())
            // Oldest message
            .body("[2].id", equalTo(expected[2].id.toString()))
            .body("[2].message", equalTo(expected[2].message))
            .body("[2].visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("[2].active", equalTo(true))
            .body("[2].parent", nullValue())
    }

    @Test
    fun `GET all should return empty list if no messages exist`() {
        given()
            .`when`()
            .get(ENDPOINT)
            .then()
            .statusCode(OK.value())
            .body("size()", `is`(0))
    }

    @Test
    fun `GET all should not populate children`() {
        val parentMessage = createTestMessage().first
        val child = createTestMessage(parent = parentMessage.id)

        given()
            .`when`()
            .get(ENDPOINT)
            .then()
            .statusCode(OK.value())
            .body("size()", `is`(1))
            // Parent
            .body("[0].id", equalTo(parentMessage.id.toString()))
            .body("[0].parent", nullValue())
            .body("[0].children.size()", `is`(0))
    }

    private fun createTestMessage(
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
