/*
 * This file is generated by jOOQ.
 */
package com.g0dkar.samplek8sproj.persistence.jooq;


import com.g0dkar.samplek8sproj.persistence.jooq.tables.Messages;
import com.g0dkar.samplek8sproj.persistence.jooq.tables.VisitorTypeEnum;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index MESSAGES_PKEY = Indexes0.MESSAGES_PKEY;
    public static final Index VISITOR_TYPE_ENUM_PKEY = Indexes0.VISITOR_TYPE_ENUM_PKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index MESSAGES_PKEY = Internal.createIndex("messages_pkey", Messages.MESSAGES, new OrderField[] { Messages.MESSAGES.ID }, true);
        public static Index VISITOR_TYPE_ENUM_PKEY = Internal.createIndex("visitor_type_enum_pkey", VisitorTypeEnum.VISITOR_TYPE_ENUM, new OrderField[] { VisitorTypeEnum.VISITOR_TYPE_ENUM.ID }, true);
    }
}
