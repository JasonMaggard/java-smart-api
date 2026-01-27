package com.jasonmaggard.smart_api.api.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointMetadata {
    private String controller;
    private String handler;
    private String path;
    private String method;
    private String fullPath;
    private List<String> paramTypes;
    private Map<String, Object> parameters;
    private Map<String, Object> bodySchema;
    private String returnType;
    private Map<String, Object> rawMetadata;
    private String handlerSource;
}
