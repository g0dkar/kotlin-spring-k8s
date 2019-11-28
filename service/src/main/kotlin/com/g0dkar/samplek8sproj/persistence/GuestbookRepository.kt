package com.g0dkar.samplek8sproj.persistence

import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.persistence.jooq.Tables.MESSAGES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class GuestbookRepository(private val jooq: DSLContext) {
    suspend fun findById(id: UUID): GuestbookMessage? =
        jooq.select(
            MESSAGES.ID,
            MESSAGES.ACTIVE,
            MESSAGES.CREATED,
            MESSAGES.MESSAGE,
            MESSAGES.VISITOR_TYPE_ID,
            MESSAGES.PARENT
        )
            .from(MESSAGES)
            .where(MESSAGES.ID.eq(id))
            .and(MESSAGES.ACTIVE.eq(true))
            .fetchOneInto(GuestbookMessage::class.java)

    suspend fun getMessages(max: Int = 50): Flow<GuestbookMessage> =
        jooq.select(
            MESSAGES.ID,
            MESSAGES.ACTIVE,
            MESSAGES.CREATED,
            MESSAGES.MESSAGE,
            MESSAGES.VISITOR_TYPE_ID,
            MESSAGES.PARENT
        )
            .from(MESSAGES)
            .where(MESSAGES.ACTIVE.eq(true))
            .orderBy(MESSAGES.CREATED.desc())
            .limit(max)
            .fetchInto(GuestbookMessage::class.java)
            .asFlow()
}