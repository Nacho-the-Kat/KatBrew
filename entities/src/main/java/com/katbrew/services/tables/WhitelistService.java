package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.WhitelistDao;
import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhitelistService extends JooqService<Whitelist, WhitelistDao> {

    public ResponseEntity<String> updateAddress(final String oldAddress, final Map<String, Object> body) {
        final List<Whitelist> entry = findBy(List.of(Tables.WHITELIST.ADDRESS.eq(oldAddress)));
        if (entry.isEmpty()) {
            return ResponseEntity.status(404).body("Address already updated or not on the whitelist");
        }

        final List<Whitelist> newEntry = findBy(List.of(Tables.WHITELIST.ADDRESS.eq(body.get("address").toString())));

        if (!newEntry.isEmpty()) {
            return ResponseEntity.status(500).body("Address already on the whitelist");
        }
        body.put("previousAddress", oldAddress);
        patchEntity(entry.get(0), body);
        return ResponseEntity.ok().build();
    }

}
