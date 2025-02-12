package com.katbrew.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.pojos.TransactionExternal;
import com.katbrew.services.tables.CodeWordingsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntityConverter {
    private final CodeWordingsService codeWordingsService;
    private Map<String, Integer> codes;

    @PostConstruct
    private void generate(){
        this.codes = codeWordingsService.getAsMapWithNull();
    }
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    public Transaction convertToDbTransaction(final TransactionExternal external){
        final Integer errorCode = codes.get(external.getOpError());
        final Integer pCode = codes.get(external.getP());
        final Integer opCode = codes.get(external.getOp());
        external.setP(pCode == null ? null : pCode.toString());
        external.setOp(opCode == null ? null : opCode.toString());
        external.setOpError(errorCode == null ? null : errorCode.toString());
        return mapper.convertValue(external, Transaction.class);
    }
}
