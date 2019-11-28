/*
 * This file is generated by jOOQ.
 */
package com.g0dkar.samplek8sproj.persistence.jooq;


import com.g0dkar.samplek8sproj.persistence.jooq.tables.Messages;
import com.g0dkar.samplek8sproj.persistence.jooq.tables.VisitorTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1326401699;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.messages</code>.
     */
    public final Messages MESSAGES = com.g0dkar.samplek8sproj.persistence.jooq.tables.Messages.MESSAGES;

    /**
     * The table <code>public.visitor_type_enum</code>.
     */
    public final VisitorTypeEnum VISITOR_TYPE_ENUM = com.g0dkar.samplek8sproj.persistence.jooq.tables.VisitorTypeEnum.VISITOR_TYPE_ENUM;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Messages.MESSAGES,
            VisitorTypeEnum.VISITOR_TYPE_ENUM);
    }
}
