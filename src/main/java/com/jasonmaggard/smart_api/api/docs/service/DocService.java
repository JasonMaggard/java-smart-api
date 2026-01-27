package com.jasonmaggard.smart_api.api.docs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import com.jasonmaggard.smart_api.api.docs.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocService {
    
    private final DocRepository docRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public Doc create(JsonNode payload) {
        Doc doc = new Doc();
        updateDocFromPayload(doc, payload);
        return docRepository.save(doc);
    }
    
    @Transactional(readOnly = true)
    public List<Doc> findAll() {
        return docRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Doc findByEndpoint(String path, String method) {
        return docRepository.findByEndpointPathAndHttpMethod(path, method.toUpperCase())
            .orElse(null);
    }
    
    @Transactional
    public Doc update(UUID id, JsonNode payload) {
        Objects.requireNonNull(id, "Documentation ID cannot be null");
        Doc doc = docRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Documentation not found with id: " + id));
        
        updateDocFromPayload(doc, payload);
        @SuppressWarnings("null") // JPA save is guaranteed to return non-null for managed entities
        Doc savedDoc = docRepository.save(doc);
        return Objects.requireNonNull(savedDoc, "Failed to save documentation");
    }
    
    private void updateDocFromPayload(Doc doc, JsonNode payload) {
        if (payload.has("endpoint_path") && payload.get("endpoint_path").isTextual()) {
            doc.setEndpointPath(payload.get("endpoint_path").asText());
        }
        
        if (payload.has("http_method") && payload.get("http_method").isTextual()) {
            doc.setHttpMethod(payload.get("http_method").asText().toUpperCase());
        }
        
        if (payload.has("description") && payload.get("description").isTextual()) {
            doc.setDescription(payload.get("description").asText());
        }
        
        if (payload.has("parameters")) {
            JsonNode parametersNode = payload.get("parameters");
            if (parametersNode.isObject() || parametersNode.isNull()) {
                doc.setParameters(jsonNodeToMap(parametersNode));
            }
        }
        
        if (payload.has("response_schema")) {
            JsonNode responseSchemaNode = payload.get("response_schema");
            if (responseSchemaNode.isObject() || responseSchemaNode.isNull()) {
                doc.setResponseSchema(jsonNodeToMap(responseSchemaNode));
            }
        }
        
        if (payload.has("code_examples")) {
            JsonNode codeExamplesNode = payload.get("code_examples");
            if (codeExamplesNode.isObject() || codeExamplesNode.isNull()) {
                doc.setCodeExamples(jsonNodeToMap(codeExamplesNode));
            }
        }
        
        if (payload.has("llm_model") && payload.get("llm_model").isTextual()) {
            doc.setLlmModel(payload.get("llm_model").asText());
        }
        
        if (payload.has("token_count") && payload.get("token_count").isNumber()) {
            doc.setTokenCount(payload.get("token_count").asInt());
        }
    }
    
    private Map<String, Object> jsonNodeToMap(JsonNode node) {
        if (node == null || node.isNull()) {
            return new HashMap<>();
        }
        return objectMapper.convertValue(node, objectMapper.getTypeFactory()
            .constructMapType(HashMap.class, String.class, Object.class));
    }
}
