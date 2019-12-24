/*
 * This file is generated by jOOQ.
 */
package com.g0dkar.samplek8sproj.persistence.jooq;


import com.g0dkar.samplek8sproj.persistence.jooq.tables.Messages;
import com.g0dkar.samplek8sproj.persistence.jooq.tables.VisitorTypeEnum;
import com.g0dkar.samplek8sproj.persistence.jooq.tables.records.MessagesRecord;
import com.g0dkar.samplek8sproj.persistence.jooq.tables.records.VisitorTypeEnumRecord;
import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import javax.annotation.processing.Generated;


/**
 * A class modelling foreign key relationships and constraints of tables of
 * the <code>public</code> schema.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<MessagesRecord> MESSAGES_PKEY = UniqueKeys0.MESSAGES_PKEY;
    public static final UniqueKey<VisitorTypeEnumRecord> VISITOR_TYPE_ENUM_PKEY = UniqueKeys0.VISITOR_TYPE_ENUM_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<MessagesRecord, MessagesRecord> MESSAGES__FK_MESSAGES___MESSAGES_PARENT = ForeignKeys0.MESSAGES__FK_MESSAGES___MESSAGES_PARENT;
    public static final ForeignKey<MessagesRecord, VisitorTypeEnumRecord> MESSAGES__FK_MESSAGES___VISITOR_TYPE_ENUM = ForeignKeys0.MESSAGES__FK_MESSAGES___VISITOR_TYPE_ENUM;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<MessagesRecord> MESSAGES_PKEY = Internal.createUniqueKey(Messages.MESSAGES, "messages_pkey", Messages.MESSAGES.ID);
        public static final UniqueKey<VisitorTypeEnumRecord> VISITOR_TYPE_ENUM_PKEY = Internal.createUniqueKey(VisitorTypeEnum.VISITOR_TYPE_ENUM, "visitor_type_enum_pkey", VisitorTypeEnum.VISITOR_TYPE_ENUM.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<MessagesRecord, MessagesRecord> MESSAGES__FK_MESSAGES___MESSAGES_PARENT = Internal.createForeignKey(com.g0dkar.samplek8sproj.persistence.jooq.Keys.MESSAGES_PKEY, Messages.MESSAGES, "messages__fk_messages___messages_parent", Messages.MESSAGES.PARENT);
        public static final ForeignKey<MessagesRecord, VisitorTypeEnumRecord> MESSAGES__FK_MESSAGES___VISITOR_TYPE_ENUM = Internal.createForeignKey(com.g0dkar.samplek8sproj.persistence.jooq.Keys.VISITOR_TYPE_ENUM_PKEY, Messages.MESSAGES, "messages__fk_messages___visitor_type_enum", Messages.MESSAGES.VISITOR_TYPE_ID);
    }
}
