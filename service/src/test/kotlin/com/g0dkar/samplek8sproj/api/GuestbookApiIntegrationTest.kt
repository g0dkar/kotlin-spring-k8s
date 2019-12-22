package com.g0dkar.samplek8sproj.api

import com.g0dkar.samplek8sproj.IntegrationTest
import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.model.VisitorType
import com.g0dkar.samplek8sproj.model.request.GuestbookMessageRequest
import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import com.g0dkar.samplek8sproj.persistence.jooq.Tables.MESSAGES
import io.restassured.RestAssured.given
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.OffsetDateTime
import java.util.Random
import java.util.UUID

internal class GuestbookApiIntegrationTest(
    @Autowired val client: WebTestClient,
    @Autowired val jooq: DSLContext
) : IntegrationTest() {

    private val MESSAGE_TEXT: String = "Test Message"
    private val MESSAGE_VISITOR_TYPE = VisitorType.values().let { it[Random().nextInt(it.size)] }

    @Test
    fun get() {
        val expected = testMessage()

        given()
            .`when`()
            .get("/guestbook/${expected.first.id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(expected.first.id.toString()))
            .body("message", equalTo(MESSAGE_TEXT))
            .body("visitor_type", equalTo(MESSAGE_VISITOR_TYPE.name))
            .body("active", equalTo(true))
    }

    @Test
    fun delete() {
    }

    @Test
    fun create() {
    }

    @Test
    fun getAll() {
    }

    private fun testMessage(
        save: Boolean = true,
        parent: UUID? = null
    ): Triple<GuestbookMessage, GuestbookMessageRequest, GuestbookMessageResponse> {
        val message = GuestbookMessage(
            id = UUID.randomUUID(),
            parent = parent,
            message = MESSAGE_TEXT,
            visitorTypeId = MESSAGE_VISITOR_TYPE.id,
            created = OffsetDateTime.now(),
            active = true
        )

        val request = GuestbookMessageRequest(
            message = MESSAGE_TEXT,
            parent = parent,
            visitorType = MESSAGE_VISITOR_TYPE
        )

        val response = runBlocking { GuestbookMessageResponse.of(message) }

        if (save) {
            jooq.insertInto(MESSAGES)
                .set(MESSAGES.ID, message.id)
                .set(MESSAGES.ACTIVE, message.active)
                .set(MESSAGES.CREATED, message.created)
                .set(MESSAGES.MESSAGE, message.message)
                .set(MESSAGES.VISITOR_TYPE_ID, message.visitorTypeId)
                .set(MESSAGES.PARENT, message.parent)
                .execute()
        }

        return Triple(message, request, response)
    }
}
