package com.g0dkar.samplek8sproj.persistence

import com.g0dkar.samplek8sproj.exception.InactiveParentException
import com.g0dkar.samplek8sproj.model.GuestbookMessage
import com.g0dkar.samplek8sproj.persistence.jooq.Tables.MESSAGES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class GuestbookRepository(private val jooq: DSLContext) {
    private fun isActive(id: UUID): Boolean =
        jooq.selectCount()
            .from(MESSAGES)
            .where(MESSAGES.ID.eq(id))
            .and(MESSAGES.ACTIVE.eq(true))
            .fetchOne(0) == 1

    fun save(message: GuestbookMessage): Boolean =
        if (message.parent != null && !isActive(message.parent)) {
            throw InactiveParentException()
        } else {
            jooq.insertInto(MESSAGES)
                .set(MESSAGES.ID, message.id)
                .set(MESSAGES.ACTIVE, message.active)
                .set(MESSAGES.CREATED, message.created)
                .set(MESSAGES.UPDATED, message.updated)
                .set(MESSAGES.MESSAGE, message.message)
                .set(MESSAGES.VISITOR_TYPE_ID, message.visitorTypeId)
                .set(MESSAGES.PARENT, message.parent)
                .execute() > 0
        }

    fun setActive(id: UUID, active: Boolean): Boolean =
        jooq.update(MESSAGES)
            .set(MESSAGES.ACTIVE, active)
            .where(MESSAGES.ID.eq(id))
            .execute() > 0

    fun findById(id: UUID): GuestbookMessage? =
        jooq.select(
            MESSAGES.ID,
            MESSAGES.ACTIVE,
            MESSAGES.CREATED,
            MESSAGES.UPDATED,
            MESSAGES.MESSAGE,
            MESSAGES.VISITOR_TYPE_ID,
            MESSAGES.PARENT
        )
            .from(MESSAGES)
            .where(MESSAGES.ID.eq(id))
            .and(MESSAGES.ACTIVE.eq(true))
            .fetchOneInto(GuestbookMessage::class.java)

    fun findByParent(id: UUID): List<GuestbookMessage> =
        jooq.select(
            MESSAGES.ID,
            MESSAGES.ACTIVE,
            MESSAGES.CREATED,
            MESSAGES.UPDATED,
            MESSAGES.MESSAGE,
            MESSAGES.VISITOR_TYPE_ID,
            MESSAGES.PARENT
        )
            .from(MESSAGES)
            .where(MESSAGES.PARENT.eq(id))
            .and(MESSAGES.ACTIVE.eq(true))
            .fetchInto(GuestbookMessage::class.java)

    fun getMessages(offset: Int = 0, max: Int = 50): List<GuestbookMessage> =
        jooq.select(
            MESSAGES.ID,
            MESSAGES.ACTIVE,
            MESSAGES.CREATED,
            MESSAGES.UPDATED,
            MESSAGES.MESSAGE,
            MESSAGES.VISITOR_TYPE_ID,
            MESSAGES.PARENT
        )
            .from(MESSAGES)
            .where(MESSAGES.ACTIVE.eq(true))
            .and(MESSAGES.PARENT.isNull)
            .orderBy(MESSAGES.CREATED.desc())
            .offset(offset)
            .limit(max)
            .fetchInto(GuestbookMessage::class.java)
}
