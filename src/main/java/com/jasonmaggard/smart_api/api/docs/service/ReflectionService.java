package com.jasonmaggard.smart_api.api.docs.service;

import com.jasonmaggard.smart_api.api.docs.dto.EndpointMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReflectionService {
    
    private final ApplicationContext applicationContext;
    private List<EndpointMetadata> endpoints = new ArrayList<>();
    
    public ReflectionService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @PostConstruct
    public void init() {
        refresh();
    }
    
    public List<EndpointMetadata> extractEndpointMetadata() {
        return endpoints;
    }
    
    public List<EndpointMetadata> refresh() {
        log.info("Refreshing endpoint metadata...");
        List<EndpointMetadata> discovered = new ArrayList<>();
        
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
        
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            
            Method method = handlerMethod.getMethod();
            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String handlerName = method.getName();
            
            // Extract path patterns
            Set<String> patterns = info.getPatternValues();
            String path = patterns.isEmpty() ? "" : patterns.iterator().next();
            
            // Extract HTTP methods
            Set<String> methods = info.getMethodsCondition().getMethods()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
            
            if (methods.isEmpty()) {
                methods = Set.of("GET"); // default
            }
            
            // Extract parameter information
            Parameter[] params = method.getParameters();
            List<String> paramTypes = Arrays.stream(params)
                .map(p -> p.getType().getSimpleName())
                .collect(Collectors.toList());
            
            // Build parameter metadata
            Map<String, Object> parametersMap = new HashMap<>();
            Map<String, Object> bodySchema = null;
            
            for (Parameter param : params) {
                if (param.isAnnotationPresent(RequestBody.class)) {
                    bodySchema = buildSchemaFromClass(param.getType());
                    parametersMap.put("body", bodySchema);
                } else if (param.isAnnotationPresent(PathVariable.class)) {
                    PathVariable pv = param.getAnnotation(PathVariable.class);
                    String name = pv.value().isEmpty() ? param.getName() : pv.value();
                    parametersMap.put(name, Map.of("type", param.getType().getSimpleName(), "in", "path"));
                } else if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam rp = param.getAnnotation(RequestParam.class);
                    String name = rp.value().isEmpty() ? param.getName() : rp.value();
                    parametersMap.put(name, Map.of("type", param.getType().getSimpleName(), "in", "query"));
                }
            }
            
            // Return type
            String returnType = method.getReturnType().getSimpleName();
            
            // Create metadata for each HTTP method
            for (String httpMethod : methods) {
                EndpointMetadata metadata = new EndpointMetadata();
                metadata.setController(controllerName);
                metadata.setHandler(handlerName);
                metadata.setPath(path);
                metadata.setMethod(httpMethod);
                metadata.setFullPath(path);
                metadata.setParamTypes(paramTypes);
                metadata.setParameters(parametersMap);
                metadata.setBodySchema(bodySchema);
                metadata.setReturnType(returnType);
                metadata.setRawMetadata(new HashMap<>());
                metadata.setHandlerSource(null); // Can't easily extract source in Java
                
                discovered.add(metadata);
                log.debug("Discovered endpoint: {} {}", httpMethod, path);
            }
        }
        
        this.endpoints = discovered;
        log.info("ReflectionService: discovered {} endpoint(s)", discovered.size());
        return discovered;
    }
    
    private Map<String, Object> buildSchemaFromClass(Class<?> clazz) {
        Map<String, Object> schema = new HashMap<>();
        
        try {
            Arrays.stream(clazz.getDeclaredFields())
                .forEach(field -> {
                    Map<String, Object> fieldInfo = new HashMap<>();
                    fieldInfo.put("type", field.getType().getSimpleName());
                    fieldInfo.put("name", field.getName());
                    schema.put(field.getName(), fieldInfo);
                });
        } catch (Exception e) {
            log.warn("Failed to build schema for class: {}", clazz.getName(), e);
        }
        
        return schema;
    }
}
