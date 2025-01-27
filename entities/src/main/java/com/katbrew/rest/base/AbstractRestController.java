package com.katbrew.rest.base;

import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.base.JooqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <T> the pojo of the dataset.
 * @param <E> the service of the dataset.
 */
@Slf4j
public abstract class AbstractRestController<T extends Serializable, E extends JooqService> implements ApplicationContextAware {

    public E service;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        this.service = applicationContext.getBean((Class<E>) actualTypeArguments[1]);
    }

    @GetMapping("/{id}")
    public ApiResponse<T> findOne(
            @PathVariable final Integer id
    ) {

        return new ApiResponse(service.findOne(id));

    }

    @PostMapping("/findByIds")
    @ResponseBody
    public ApiResponse<List<T>> findByIds(
            @RequestBody final List<Integer> ids
    ) {
        return new ApiResponse(
                ids
                        .stream()
                        .map(service::findOne)
                        .collect(Collectors.toList()));
    }

    @GetMapping
    public ApiResponse<List<T>> findAll() {
        return new ApiResponse(service.findAll());
    }

    @GetMapping("/byOffset")
    public ApiResponse<List<?>> findByOffset(
            @RequestParam final int start,
            @RequestParam final int max
    ) {
        return new ApiResponse(service.findWithOffset(start, max));
    }

}
