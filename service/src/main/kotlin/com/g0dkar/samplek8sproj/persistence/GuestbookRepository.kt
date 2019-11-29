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
    suspend fun save(message: GuestbookMessage): Boolean =
        jooq.insertInto(MESSAGES)
            .set(MESSAGES.ID, message.id)
            .set(MESSAGES.ACTIVE, message.active)
            .set(MESSAGES.CREATED, message.created)
            .set(MESSAGES.MESSAGE, message.message)
            .set(MESSAGES.VISITOR_TYPE_ID, message.visitorType.id)
            .set(MESSAGES.PARENT, message.parent)
            .execute() > 0

    suspend fun setActive(id: UUID, active: Boolean): Boolean =
        jooq.update(MESSAGES)
            .set(MESSAGES.ACTIVE, active)
            .where(MESSAGES.ID.eq(id))
            .execute() > 0

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

    suspend fun getMessages(offset: Int = 0, max: Int = 50): Flow<GuestbookMessage> =
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
            .offset(offset)
            .limit(max)
            .fetchInto(GuestbookMessage::class.java)
            .asFlow()
}
