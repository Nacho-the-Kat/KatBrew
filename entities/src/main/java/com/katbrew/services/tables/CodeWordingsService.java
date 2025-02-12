package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.CodeWordingsDao;
import com.katbrew.entities.jooq.db.tables.pojos.CodeWordings;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeWordingsService extends JooqService<CodeWordings, CodeWordingsDao> {
    public Map<String, Integer> getAsMapWithNull(){
        final Map<String, Integer> map = findAll().stream().collect(Collectors.toMap(CodeWordings::getIdentifier, CodeWordings::getId));
        map.put("", null);
        return map;
    }
}
