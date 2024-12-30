package com.katbrew.rest.admin;

import com.katbrew.services.base.JooqService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class BaseAdminRestController<T extends Serializable, E extends JooqService> implements ApplicationContextAware {

    public E service;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        this.service = applicationContext.getBean((Class<E>) actualTypeArguments[1]);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Integer id) {
        service.delete(id);
    }

    @PostMapping("/create")
    public T create(@RequestBody final T body) {
        return (T) service.insert(body);
    }

    @PostMapping("/createAll")
    public List<T> create(@RequestBody final List<T> body) {
        return (List<T>) service.insert(body);
    }

    @PostMapping("/patch/{id}")
    public void patch(@RequestBody final Map<String, Object> fieldMap, @PathVariable final String id) {
        Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        if (ReflectionUtils.findField(type, "id").getType().getName().contains("BigInteger")) {
            service.patch(new BigInteger(id), fieldMap);
        } else {
            service.patch(Integer.parseInt(id), fieldMap);
        }
    }

    @PostMapping("/update")
    public void update(@RequestBody final T body) {
        service.update(body);
    }

    @PostMapping("/updateAll")
    public void update(@RequestBody final List<T> body) {
        service.update(body);
    }
}
