package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.FetchDataDao;
import com.katbrew.entities.jooq.db.tables.pojos.FetchData;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchDataService extends JooqService<FetchData, FetchDataDao> {

    public FetchData findByIdentifier(String identifier) {
        final List<FetchData> last = this.findBy(List.of(Tables.FETCH_DATA.IDENTIFIER.eq(identifier)));
        if (last.isEmpty()) {
            return null;
        }
        return last.get(0);
    }
}
