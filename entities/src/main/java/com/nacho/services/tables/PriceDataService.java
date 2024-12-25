//package com.nacho.services.tables;
//
//import com.nacho.entities.jooq.db.Tables;
//import com.nacho.entities.jooq.db.tables.daos.PricedataDao;
//import com.nacho.entities.jooq.db.tables.pojos.Pricedata;
//import com.nacho.entities.jooq.db.tables.records.PricedataRecord;
//import com.nacho.services.base.AbstractJooqService;
//import org.jooq.Condition;
//import org.jooq.Configuration;
//import org.jooq.DSLContext;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@Service
//public class PriceDataService extends AbstractJooqService<Pricedata, PricedataRecord> {
//    public PriceDataService(final DSLContext context) {
//        super(new PricedataDao(), new PricedataRecord(), Pricedata.class, context);
//    }
//
//    public List<Pricedata> getTokenPriceData(final String tick, final LocalDateTime start, final LocalDateTime end) {
//        List<Condition> conditions = new ArrayList<>();
//        if (start != null){
//            conditions.add(Tables.PRICEDATA.TIMESTAMP.ge(start));
//        }
//        if (end != null){
//            conditions.add(Tables.PRICEDATA.TIMESTAMP.le(end));
//        }
//        conditions.add(Tables.PRICEDATA.TICK.eq(tick));
//        return new ArrayList<>();
////        return this.findBy(conditions, Collections.singletonList(Tables.PRICEDATA.TIMESTAMP.asc()));
//    }
//}
