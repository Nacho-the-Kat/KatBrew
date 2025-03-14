package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/transactions")
@RequiredArgsConstructor
public class TransactionRestController extends AbstractRestController<Transaction, TransactionService> {

    private final TransactionService transactionService;

    @GetMapping("/mint-totals")
    public List getMintTotals(@RequestParam(required = false) final String start, @RequestParam(required = false) final String end) {
        return transactionService.getMintsTotal(start, end);
    }

    @Override
    public ApiResponse<List<Transaction>> findAll() {
        return new ApiResponse<>(500, "Not allowed on transactions");
    }
}
