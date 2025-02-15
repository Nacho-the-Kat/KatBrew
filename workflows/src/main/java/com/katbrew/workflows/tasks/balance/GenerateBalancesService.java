package com.katbrew.workflows.tasks.balance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.BalanceService;
import com.katbrew.services.tables.CodeWordingsService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateBalancesService {
    private final TokenService tokenService;
    private final BalanceService balanceService;
    private final LastUpdateService lastUpdateService;
    private final CodeWordingsService codeWordingsService;
    private final DSLContext dsl;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();
    final ConcurrentHashMap<String, Balance> balances = new ConcurrentHashMap<>();

    public void generateBalances() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final String identifier = "balancesLastOpScore";
            final LastUpdate lastUpdate = lastUpdateService.findByIdentifier(identifier);
            final Map<String, Integer> codes = codeWordingsService.getAsMapWithNull();
            BigInteger opScore = lastUpdate.getData() != null ? new BigInteger(lastUpdate.getData()) : null;
            do {
                log.info("opscore: " + (opScore != null ? opScore.toString() : "null"));
                final Map<String, Boolean> toUpdate = new HashMap<>();

                final List<Transaction> transactions = dsl.selectFrom(Tables.TRANSACTION)
                        .where(Tables.TRANSACTION.OP_SCORE.gt(opScore == null ? BigInteger.ZERO : opScore).and(Tables.TRANSACTION.OP_ERROR.isNull()))
                        .orderBy(Tables.TRANSACTION.OP_SCORE.asc())
                        .limit(1000000)
                        .fetch()
                        .into(Transaction.class);


                for (final Transaction single : transactions) {
                    try {
                        if (single.getOp().equals(codes.get("deploy"))) {
                            final Token token = tokenService.findOne(single.getFkToken());
                            if (!token.getPre().equals(BigInteger.ZERO)) {
                                Balance deploy = getBalance(single.getFkToken(), single.getFromAddress());
                                if (deploy == null) {
                                    generateNewBalance(token.getPre(), single.getFkToken(), single.getFromAddress());
                                } else {
                                    deploy.setBalance(token.getPre());
                                    toUpdate.put(single.getFromAddress() + "-" + single.getFkToken(), true);
                                }
                            }
                        }
                        if (single.getOp().equals(codes.get("mint"))) {
                            Balance mint = getBalance(single.getFkToken(), single.getFromAddress());
                            if (mint == null) {
                                generateNewBalance(single.getAmt(), single.getFkToken(), single.getFromAddress());
                            } else {
                                try {
                                    mint.setBalance(mint.getBalance().add(single.getAmt()));
                                    toUpdate.put(single.getFromAddress() + "-" + single.getFkToken(), true);
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                }
                            }
                        }
                        if (single.getOp().equals(codes.get("transfer"))) {
                            Balance from = getBalance(single.getFkToken(), single.getFromAddress());
                            Balance to = getBalance(single.getFkToken(), single.getToAddress());

                            if (from == null) {
                                //fallback for missing data
                                generateNewBalance(single.getAmt(), single.getFkToken(), single.getFromAddress());
                                from = getBalance(single.getFkToken(), single.getFromAddress());
                                log.info(single.getFromAddress() + " providing ghost data for transaction " + single.getHashRev());
                            }

                            if (from.getBalance() != null && !from.getBalance().equals(BigInteger.ZERO)) {
                                from.setBalance(from.getBalance().subtract(single.getAmt()));
                            }
                            if (to == null) {
                                generateNewBalance(single.getAmt(), single.getFkToken(), single.getToAddress());
                            } else {
                                to.setBalance(to.getBalance().add(single.getAmt()));
                                toUpdate.put(single.getToAddress() + "-" + single.getFkToken(), true);
                            }
                        }
                    } catch (Exception e) {
                        try {
                            log.error(e.getMessage());
                            log.error(mapper.writeValueAsString(single));
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }
                        throw new RuntimeException(e);
                    }
                }
                if (!transactions.isEmpty()) {
                    opScore = transactions.get(transactions.size() - 1).getOpScore();
                    lastUpdate.setData(opScore.toString());
                } else {
                    opScore = null;
                }
                final List<Balance> created = balanceService.batchInsert(balances.values().stream().filter(single -> single.getId() == null).toList());
                //todo check list takes longer as updateing?
                balanceService.batchUpdate(balances.values().stream().filter(single -> toUpdate.containsKey(single.getHolderId() + "-" + single.getFkToken())).toList());
                created.forEach(single -> balances.put(single.getHolderId() + "-" + single.getFkToken(), single));
                log.info("opscore finished: " + (opScore != null ? opScore.toString() : "null"));
            } while (opScore != null);

            final List<Balance> toDelete = dsl.selectFrom(Tables.BALANCE)
                    .where(Tables.BALANCE.BALANCE_.isNull().or(Tables.BALANCE.BALANCE_.eq(BigInteger.ZERO)))
                    .fetch()
                    .into(Balance.class);
            balanceService.batchDelete(toDelete);
            lastUpdateService.update(lastUpdate);
            balances.clear();
        });
        executorService.shutdown();
    }

    private void generateNewBalance(final BigInteger amt, final Integer fkToken, final BigInteger address) {
        if (amt == null) {
            log.error("strange");
        }
        final Balance newOne = new Balance();
        newOne.setBalance(amt);
        newOne.setFkToken(fkToken);
        newOne.setHolderId(address);
        balances.put(address + "-" + fkToken, newOne);
    }

    private Balance getBalance(final Integer fkToken, final BigInteger address) {
        if (balances.containsKey(address + "-" + fkToken)) {
            return balances.get(address + "-" + fkToken);
        }
        final Balance balance = balanceService.findByTokenAndHolderId(address, fkToken);
        if (balance != null) {
            balances.put(address + "-" + fkToken, balance);
            return balances.get(address + "-" + fkToken);
        }
        return null;
    }
}
