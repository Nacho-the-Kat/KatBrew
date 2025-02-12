package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.BalanceDao;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService extends JooqService<Balance, BalanceDao> {
    public Balance findByTokenAndHolderId(final BigInteger address, final Integer tokenId) {
        final List<Balance> balanceList = findBy(List.of(
                Tables.BALANCE.FK_TOKEN.eq(tokenId).and(Tables.BALANCE.HOLDER_ID.eq(address))
        ));
        if (balanceList.isEmpty()) {
            return null;
        }
        return balanceList.get(0);
    }
}
