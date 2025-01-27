package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/transactions")
@RequiredArgsConstructor
public class TransactionAdminRestController extends BaseAdminRestController<Transaction, TransactionService> {
}
