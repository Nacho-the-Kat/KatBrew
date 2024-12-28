package com.katbrew.services.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.exceptions.NotValidException;
import com.katbrew.helper.KatBrewObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.max;

/**
 * the base jooq service for the database.
 *
 * @param <T> the pojo of the dataset.
 * @param <D> the dao of the dataset.
 */
@SuppressWarnings("unchecked")
@Slf4j
@Transactional
public abstract class JooqService<T extends Serializable, D extends DAOImpl> {

    public static final String MULTICAST_PREFIX = "/multicast/";
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private DAOImpl dao;

    @Autowired
    private DSLContext dsl;
    private final Class<T> type;

    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    public JooqService() {
        java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        this.type = (Class<T>) actualTypeArguments[0];
        java.lang.reflect.Type intDao = actualTypeArguments[1];
        try {
            this.dao = ((Class<D>) intDao).getDeclaredConstructor().newInstance();

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error("error while creating the dao", e);
        }
    }

    @PostConstruct
    private void setConfiguration() {
        dao.setConfiguration(dsl.configuration());
    }

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

    public T findLast() {
        return findLast("id");
    }

    public T findLast(final String indexColumn) {
        Field<?> field = getField(indexColumn);
        Record1<?> result = dsl
                .select(max(field))
                .from(dao.getTable())
                .fetchOne();
        if (result != null) {
            return findOne((int) result.value1());
        }
        return null;
    }

    public List<T> findBy(final List<Condition> conditions) {
        return dsl
                .select()
                .from(dao.getTable())
                .where(conditions)
                .fetch()
                .into(type);
    }

    public List<T> findByWithOffset(List<Condition> conditions, int start, int max) {
        return dsl.select()
                .from(dao.getTable())
                .where(conditions)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }

    public List<T> findWithOffset(int start, int max) {
        return dsl.select()
                .from(dao.getTable())
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }

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

    public List<T> findAllSorted(
            final String sortBy,
            final String sortOrder
    ) {
        final Field<?> sort = getFieldWithSort(sortBy, sortOrder);

        return dsl.selectFrom(dao.getTable())
                .orderBy(sort)
                .fetch()
                .into(type);
    }

    public List<T> findBySortedLimitOffset(
            final Integer limit,
            final Integer offset,
            final String sortBy,
            final String sortOrder
    ) {
        final Field<?> sort = getFieldWithSort(sortBy, sortOrder);

        return dsl.selectFrom(dao.getTable())
                .orderBy(sort)
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(type);
    }

    public T insert(final T entityToInsert) {
        //todo
        try {
            final Record inserted = dsl.insertInto(dao.getTable()).set(dsl.newRecord(dao.getTable(), entityToInsert)).returning().fetchOne();
            T internal;
            try {
                internal = inserted.into(type);
            } catch (Exception e) {
                //fallback parsing, if the default fails
                internal = mapper.convertValue(inserted.intoMap(), type);
            }
            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.INSERT), internal);
            return internal;
        } catch (Exception e) {
            IllegalArgumentException ex = new IllegalArgumentException(e.getMessage());
            ex.setStackTrace(new StackTraceElement[0]);
            throw ex;
        }
    }

    public List<T> insert(List<T> entitiesToInsert) {
        return entitiesToInsert.stream().map(this::insert).toList();
    }

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


    public void delete(final Integer id) {
        final T entityToDelete = findOne(id);
        dao.deleteById(id);
        this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.DELETE), entityToDelete);
    }

    public void delete(List<T> entitiesToDelete) {
        dao.delete(entitiesToDelete);
        for (final T entityToDelete : entitiesToDelete) {
            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.DELETE), entityToDelete);
        }

    }

    public void delete(T entityToDelete) {
        dao.delete(entityToDelete);
        this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.DELETE), entityToDelete);
    }

    public void delete(final Long id) {
        this.delete(id.intValue());
    }

    public void patch(Integer id, Map<String, Object> fieldmap) {
        T entity = findOne(id);
        if (entity == null) {
            throw new NotValidException("No entry for the id");
        }
        fieldmap.keySet().forEach(k -> {
            java.lang.reflect.Field field = ReflectionUtils.findField(entity.getClass(), k);
            if (field != null) {
                Method method = ReflectionUtils.findMethod(entity.getClass(), "set" + StringUtils.capitalize(k), field.getType());
                if (method != null) {
                    ReflectionUtils.invokeMethod(method, entity, fieldmap.get(k));
                }
            }
        });
        this.update(entity);
    }

    public String getMulticastUrl(final MULTICAST_TYPE multicastType) {
        return MULTICAST_PREFIX + dao.getTable().getName() + multicastType.getUrlSuffix();
    }

    @Getter
    public enum MULTICAST_TYPE {

        UPDATE("/update"),

        INSERT("/insert"),


        DELETE("/delete");

        private final String urlSuffix;

        MULTICAST_TYPE(final String urlSuffix) {
            this.urlSuffix = urlSuffix;
        }
    }

    private Field<?> getFieldWithSort(final String col, final String sortDirection) {
        final Field<?> sort = getField(col);

        if (sortDirection.equals("asc")) {
            sort.asc();
        } else {
            sort.desc();
        }
        return sort;
    }

    private Field<?> getField(final String col) {
        final Field<?> field = dao.getTable().field(convertToDBCol(col));
        if (field == null) {
            throw new NotValidException("field not found");
        }
        return field;
    }

    private String convertToDBCol(final String col) {
        final StringBuilder str = new StringBuilder();
        for (int i = 0; i < col.length(); i++) {
            char internal = col.charAt(i);
            if (Character.isUpperCase(internal)) {
                str.append('_');
                str.append(Character.toLowerCase(internal));
            } else {
                str.append(internal);
            }
        }
        return str.toString();
    }
}
