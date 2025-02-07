package com.katbrew.pojos;

import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NFTCollectionExternal extends NftCollection {
    String from;
    String to;
    String tick;
}