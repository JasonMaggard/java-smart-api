package com.jasonmaggard.smart_api.api.docs.service;

import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import com.jasonmaggard.smart_api.api.docs.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocService {
    
    private final DocRepository docRepository;
    
    @Transactional
    public Doc create(Map<String, Object> payload) {
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
    public Doc update(UUID id, Map<String, Object> payload) {
        Doc doc = docRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Documentation not found with id: " + id));
        
        updateDocFromPayload(doc, payload);
        return docRepository.save(doc);
    }
    
    private void updateDocFromPayload(Doc doc, Map<String, Object> payload) {
        if (payload.containsKey("endpoint_path")) {
            doc.setEndpointPath((String) payload.get("endpoint_path"));
        }
        if (payload.containsKey("http_method")) {
            doc.setHttpMethod(((String) payload.get("http_method")).toUpperCase());
        }
        if (payload.containsKey("description")) {
            doc.setDescription((String) payload.get("description"));
        }
        if (payload.containsKey("parameters")) {
            doc.setParameters((Map<String, Object>) payload.get("parameters"));
        }
        if (payload.containsKey("response_schema")) {
            doc.setResponseSchema((Map<String, Object>) payload.get("response_schema"));
        }
        if (payload.containsKey("code_examples")) {
            doc.setCodeExamples((Map<String, Object>) payload.get("code_examples"));
        }
        if (payload.containsKey("llm_model")) {
            doc.setLlmModel((String) payload.get("llm_model"));
        }
        if (payload.containsKey("token_count")) {
            Object tokenCount = payload.get("token_count");
            doc.setTokenCount(tokenCount != null ? ((Number) tokenCount).intValue() : null);
        }
    }
}
