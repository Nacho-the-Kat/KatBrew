package com.katbrew.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class NFTCollectionInfoInternal implements Serializable {
    private String name;
    private String description;
    private String image;
    private Map<String, Object> properties;
    private Map<String, String> extensions;
}