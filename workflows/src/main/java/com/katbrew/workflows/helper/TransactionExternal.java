package com.katbrew.workflows.helper;

import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionExternal extends Transaction {
    String from;
    String to;
}