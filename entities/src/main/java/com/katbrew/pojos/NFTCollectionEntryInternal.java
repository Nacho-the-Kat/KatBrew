package com.katbrew.pojos;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
public class NFTCollectionEntryInternal implements Serializable {
    private BigInteger id;
    private BigInteger fkCollection;
    private String name;
    private String description;
    private String image;
    private List<Map<String, Object>> attributes;
    private Integer edition;
}