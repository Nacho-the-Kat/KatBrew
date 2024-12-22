package com.nacho.services.base;

import com.nacho.exceptions.NotValidException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.max;

/**
 * Base Service für alle Datenbank Entities.
 *
 * @param <T> Das Pojo der Datenbank Tabelle.
 * @param <R> Der Record der Datenbank Tabelle.
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Slf4j
public abstract class BaseEntityService<T extends Serializable, R extends Record> {
    
    public static final String MULTICAST_PREFIX = "/multicast/";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Getter
    private final DAOImpl dao;
    
    @Getter
    private final R record;

    private final Configuration configuration;

    private DSLContext dsl;

    private final Class<T> type = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[0];
    
    @PostConstruct
    private void setConfiguration() {
        dao.setConfiguration(configuration);
    }

    @PostConstruct
    private void setDsl(){
        dsl = DSL.using(configuration);
    }
    
    
    public List<T> findBy(final List<Condition> conditions) {
        return configuration
                .dsl()
                .select()
                .from(dao.getTable())
                .where(conditions)
                .fetch()
                .into(type);
    }
    
    
    public List<T> findBy(List<Condition> conditions, int start, int max) {
        return configuration
                .dsl()
                .select()
                .from(dao.getTable())
                .where(conditions)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }
    
    public List<T> findBy(final List<Condition> conditions, final List<OrderField<?>> orderFields) {
        return configuration
                .dsl()
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
        return configuration
                .dsl()
                .select()
                .from(dao.getTable())
                .where(conditions)
                .orderBy(orderFields)
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
    }

    public List<T> getByOffsetDesc(
            final int start,
            final int max
    ) {
        return configuration
                .dsl()
                .select()
                .from(dao.getTable())
                .orderBy(DSL.field("id").desc())
                .offset(start)
                .limit(max)
                .fetch()
                .into(type);
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
    
    public T findLast(final Field<?> field) {
        final Object entityId = configuration
                .dsl()
                .select(max(field))
                .from(dao.getTable())
                .fetch()
                .getValue(0, 0);
        if (entityId != null) {
            return findOne((int) entityId);
        }
        return null;
    }
    
    public T findLast(Field<?> field, final List<Condition> conditions) {
        final Object entityId = configuration
                .dsl()
                .select(max(field))
                .from(dao.getTable())
                .where(conditions)
                .fetch()
                .getValue(0, 0);

        if (entityId != null) {
            return findOne((int) entityId);
        }
        return null;
    }
    
    
    public void insert(final T entityToInsert) {
        try {
            dao.insert(entityToInsert);
            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.INSERT), entityToInsert);
        } catch (Exception e) {
            IllegalArgumentException ex = new IllegalArgumentException(e.getMessage().substring(e.getMessage().indexOf(';')));
            ex.setStackTrace(new StackTraceElement[0]);
            throw ex;
        }
    }

    public void insert(List<T> entitiesToInsert) {
        dao.insert(entitiesToInsert);
        for (final T entityToInsert : entitiesToInsert) {
            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.INSERT), entityToInsert);
        }
    }
    
    
    public void update(final T entityToUpdate) {
        dao.update(entityToUpdate);
        this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.UPDATE), entityToUpdate);
    }
    
    
    public void update(List<T> entitiesToUpdate) {
        dao.update(entitiesToUpdate);
        for (final T entityToUpdate : entitiesToUpdate) {
            this.simpMessagingTemplate.convertAndSend(getMulticastUrl(MULTICAST_TYPE.UPDATE), entityToUpdate);
        }
    }

    
    public List<T> findAll() {
        return dao.findAll();
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

    public void delete(final Long id) {
        delete(id.intValue());
    }

    public void patch(Integer id, Map<String, Object> fieldmap) {
        T entity = findOne(id);
        if (entity == null) {
            throw new NotValidException("Kein eintrag");
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
        return MULTICAST_PREFIX + getDao().getTable().getName() + multicastType.getUrlSuffix();
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
