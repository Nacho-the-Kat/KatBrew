package com.katbrew.services.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

/**
 * Base Service für alle Datenbank Entities.
 *
 * @param <T> Das Pojo der Datenbank Tabelle.
 * @param <R> Der Record der Datenbank Tabelle.
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Slf4j
@Transactional
public abstract class JooqService<T extends Serializable, R extends Record> {

    public static final String MULTICAST_PREFIX = "/multicast/";
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final DAOImpl dao;

    private final DSLContext dsl;
    private final Class<T> type = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[0];

    //    @PostConstruct
//    private void setConfiguration() {
//        dao.setConfiguration(configuration);
//    }
//
//    @PostConstruct
//    private void setDsl(){
//        dsl = DSL.using(configuration);
//    }
//
//
    public T findOne(final Integer id) throws IllegalArgumentException {

        final Object entity = dao.findById(id);

        if (type.isInstance(entity)) {
            return type.cast(entity);
        }

        throw new IllegalArgumentException("Error while search for an entity with the given id in the given service.");

    }

    public T findOne(final Long id) throws IllegalArgumentException {
        return findOne(id.intValue());
    }

    //
//    public T findLast(final Field<?> field) {
//        final Object entityId = configuration
//                .dsl()
//                .select(max(field))
//                .from(dao.getTable())
//                .fetch()
//                .getValue(0, 0);
//        if (entityId != null) {
//            return findOne((int) entityId);
//        }
//        return null;
//    }
//
    public List<T> findBy(final List<Condition> conditions) {
        return dsl
                .select()
                .from(dao.getTable())
                .where(conditions)
                .fetch()
                .into(type);
    }

    //
//
//    public List<T> findBy(List<Condition> conditions, int start, int max) {
//        return configuration
//                .dsl()
//                .select()
//                .from(dao.getTable())
//                .where(conditions)
//                .offset(start)
//                .limit(max)
//                .fetch()
//                .into(type);
//    }
//
    public List<T> findBy(final List<Condition> conditions, final List<OrderField<?>> orderFields) {
        return dsl
                .select()
                .from(dao.getTable())
                .where(conditions)
                .orderBy(orderFields)
                .fetch()
                .into(type);
    }

    public List<T> findBy(
            final List<Condition> conditions,
            final List<OrderField<?>> orderFields,
            final int start,
            final int max
    ) {
        return dsl.selectFrom(dao.getTable())
                .where(conditions)
                .orderBy(orderFields)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }

    public List<T> findBy(
            final List<OrderField<?>> orderFields,
            final int start,
            final int max
    ) {
        return dsl.selectFrom(dao.getTable())
                .orderBy(orderFields)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }

    public List<T> findBy(
            final OrderField<?> orderFields,
            final int start,
            final int max
    ) {
        return dsl.selectFrom(dao.getTable())
                .orderBy(orderFields)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }
//
//    public List<T> getByOffsetDesc(
//            final int start,
//            final int max
//    ) {
//        return configuration
//                .dsl()
//                .select()
//                .from(dao.getTable())
//                .orderBy(DSL.field("id").desc())
//                .offset(start)
//                .limit(max)
//                .fetch()
//                .into(type);
//    }
//
//    public T findLast(Field<?> field, final List<Condition> conditions) {
//        final Object entityId = configuration
//                .dsl()
//                .select(max(field))
//                .from(dao.getTable())
//                .where(conditions)
//                .fetch()
//                .getValue(0, 0);
//
//        if (entityId != null) {
//            return findOne((int) entityId);
//        }
//        return null;
//    }


    public T insert(final T entityToInsert) {
        try {
            final T internal = Objects.requireNonNull(dsl.insertInto(dao.getTable()).set(dsl.newRecord(dao.getTable(), entityToInsert)).returning().fetchOne()).into(type);

            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.INSERT), internal);
            return internal;
        } catch (Exception e) {
            IllegalArgumentException ex = new IllegalArgumentException(e.getMessage().substring(e.getMessage().indexOf(';')));
            ex.setStackTrace(new StackTraceElement[0]);
            throw ex;
        }
    }

    public List<T> insert(List<T> entitiesToInsert) {
        return entitiesToInsert.stream().map(this::insert).toList();
    }


    //todo because error
    public void update(final T entityToUpdate) {
        dao.update(entityToUpdate);
        this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.UPDATE), entityToUpdate);
    }

    public void update(List<T> entitiesToUpdate) {
        entitiesToUpdate.forEach(this::update);
    }


    public List<T> findAll() {
        return dsl.selectFrom(dao.getTable()).fetch().into(type);
    }

    public List<T> findAll(List<Field<?>> fields) {
        return dsl.select(fields).from(dao.getTable()).fetch().into(type);
    }
//
//
//    public void delete(final Integer id) {
//        final T entityToDelete = findOne(id);
//        dao.deleteById(id);
//        this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.DELETE), entityToDelete);
//    }
//
//
//    public void delete(List<T> entitiesToDelete) {
//        dao.delete(entitiesToDelete);
//        for (final T entityToDelete : entitiesToDelete) {
//            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.DELETE), entityToDelete);
//        }
//    }
//
//    public void delete(final Long id) {
//        delete(id.intValue());
//    }
//
//    public void patch(Integer id, Map<String, Object> fieldmap) {
//        T entity = findOne(id);
//        if (entity == null) {
//            throw new NotValidException("Kein eintrag");
//        }
//        fieldmap.keySet().forEach(k -> {
//            java.lang.reflect.Field field = ReflectionUtils.findField(entity.getClass(), k);
//            if (field != null) {
//                Method method = ReflectionUtils.findMethod(entity.getClass(), "set" + StringUtils.capitalize(k), field.getType());
//                if (method != null) {
//                    ReflectionUtils.invokeMethod(method, entity, fieldmap.get(k));
//                }
//            }
//        });
//        this.update(entity);
//    }


    public String getMulticastUrl(final MULTICAST_TYPE multicastType) {
        return MULTICAST_PREFIX + dao.getTable().getName() + multicastType.getUrlSuffix();
    }

    public enum MULTICAST_TYPE {

        UPDATE("/update"),

        INSERT("/insert"),


        DELETE("/delete");

        @Getter
        private final String urlSuffix;

        MULTICAST_TYPE(final String urlSuffix) {
            this.urlSuffix = urlSuffix;
        }
    }
}
