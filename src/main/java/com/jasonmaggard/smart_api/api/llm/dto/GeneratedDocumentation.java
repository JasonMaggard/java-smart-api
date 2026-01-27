package com.jasonmaggard.smart_api.api.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedDocumentation {
    
    private String description;
    private Map<String, Object> parameters;
    private Map<String, Object> examples;
    private String model;
    private Integer tokenCount;
}
