package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.LastUpdateDao;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LastUpdateService extends JooqService<LastUpdate, LastUpdateDao> {

    public LastUpdate findByIdentifier(String identifier) {
        final List<LastUpdate> last = this.findBy(List.of(Tables.LAST_UPDATE.IDENTIFIER.eq(identifier)));
        if (last.isEmpty()){
            return null;
        }
        return last.get(0);
    }
}
