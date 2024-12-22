/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db;


import com.nacho.entities.jooq.db.tables.Holder;
import com.nacho.entities.jooq.db.tables.Pricedata;
import com.nacho.entities.jooq.db.tables.Transaction;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index HOLDER_ADDRESS_KEY = Internal.createIndex(DSL.name("Holder_address_key"), Holder.HOLDER, new OrderField[] { Holder.HOLDER.ADDRESS }, true);
    public static final Index PRICEDATA_TICK_TIMESTAMP_IDX = Internal.createIndex(DSL.name("PriceData_tick_timestamp_idx"), Pricedata.PRICEDATA, new OrderField[] { Pricedata.PRICEDATA.TICK, Pricedata.PRICEDATA.TIMESTAMP }, false);
    public static final Index TRANSACTION_TICK_MTSADD_IDX = Internal.createIndex(DSL.name("Transaction_tick_mtsAdd_idx"), Transaction.TRANSACTION, new OrderField[] { Transaction.TRANSACTION.TICK, Transaction.TRANSACTION.MTSADD }, false);
}
