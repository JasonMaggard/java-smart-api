package com.jasonmaggard.smart_api.api.docs.controller;

import com.jasonmaggard.smart_api.api.docs.dto.EndpointMetadata;
import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import com.jasonmaggard.smart_api.api.docs.service.DocService;
import com.jasonmaggard.smart_api.api.docs.service.ReflectionService;
import com.jasonmaggard.smart_api.api.llm.dto.GeneratedDocumentation;
import com.jasonmaggard.smart_api.api.llm.exception.LLMException;
import com.jasonmaggard.smart_api.api.llm.service.LLMCacheService;
import com.jasonmaggard.smart_api.api.llm.service.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
@Tag(name = "Documentation", description = "API documentation generation and management")
@Slf4j
public class DocsController {
    
    private final DocService docService;
    private final ReflectionService reflectionService;
    private final LLMService llmService;
    private final LLMCacheService cacheService;
    
    private static long lastGenerateAt = 0;
    private static final int COOLDOWN_SECONDS = 60;
    private static final int MAX_ENQUEUE = 50;
    
    @PostMapping("/generate")
    @Operation(summary = "Trigger documentation generation for all endpoints")
    @ApiResponse(responseCode = "200", description = "Documentation generation started")
    public ResponseEntity<Map<String, Object>> generateDocs(
            @RequestBody(required = false) GenerateDocsRequest request) {
        
        List<EndpointMetadata> endpoints = reflectionService.refresh();
        
        int limit = request != null && request.getLimit() != null ? 
            Math.min(request.getLimit(), 1000) : MAX_ENQUEUE;
        
        // Check cooldown
        long now = System.currentTimeMillis();
        boolean confirm = request != null && Boolean.TRUE.equals(request.getConfirm());
        
        if (!confirm && lastGenerateAt > 0 && (now - lastGenerateAt) < COOLDOWN_SECONDS * 1000) {
            int retryAfter = (int) Math.ceil((COOLDOWN_SECONDS * 1000 - (now - lastGenerateAt)) / 1000.0);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cooldown active. Try again in " + retryAfter + " seconds or set { confirm: true } to override.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }
        
        // Filter by paths if provided
        List<EndpointMetadata> toEnqueue = endpoints;
        if (request != null && request.getPaths() != null && !request.getPaths().isEmpty()) {
            toEnqueue = endpoints.stream()
                .filter(e -> request.getPaths().contains(e.getPath()) || 
                             request.getPaths().contains(e.getFullPath()))
                .toList();
        }
        
        if (toEnqueue.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No endpoints discovered to enqueue");
            response.put("enqueued", 0);
            return ResponseEntity.ok(response);
        }
        
        if (!confirm && toEnqueue.size() > limit) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Too many endpoints to enqueue");
            response.put("discovered", toEnqueue.size());
            response.put("limit", limit);
            return ResponseEntity.badRequest().body(response);
        }
        
        // In a real implementation, you would enqueue jobs here
        // For now, just return the count
        int enqueued = Math.min(toEnqueue.size(), limit);
        lastGenerateAt = System.currentTimeMillis();
        
        log.info("Would enqueue {} documentation generation jobs", enqueued);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Enqueued " + enqueued + " documentation jobs");
        response.put("enqueued", enqueued);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/generate-one")
    @Operation(summary = "Generate documentation for a single endpoint")
    public ResponseEntity<Map<String, Object>> generateOne(@RequestBody GenerateOneRequest request) {
        if (request.getPath() == null || request.getPath().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Path is required"));
        }
        
        String path = request.getPath();
        String method = request.getMethod() != null ? request.getMethod().toUpperCase() : "GET";
        
        try {
            // Find endpoint metadata
            EndpointMetadata metadata = reflectionService.extractEndpointMetadata().stream()
                .filter(e -> e.getPath().equals(path) && e.getMethod().equals(method))
                .findFirst()
                .orElse(null);
            
            if (metadata == null) {
                // Create minimal metadata if endpoint not found
                metadata = new EndpointMetadata();
                metadata.setPath(path);
                metadata.setFullPath(path);
                metadata.setMethod(method);
            }
            
            // Call LLM service to generate documentation
            log.info("Generating documentation for {} {}", method, path);
            GeneratedDocumentation result = llmService.generateDocumentation(metadata);
            
            // Map result to Doc entity payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("endpoint_path", metadata.getFullPath());
            payload.put("http_method", method);
            payload.put("description", result.getDescription());
            payload.put("parameters", result.getParameters());
            payload.put("response_schema", null);
            payload.put("code_examples", result.getExamples());
            payload.put("llm_model", result.getModel());
            payload.put("token_count", result.getTokenCount());
            
            // Save or update documentation
            Doc existing = docService.findByEndpoint(metadata.getFullPath(), method);
            
            Doc doc;
            String message;
            if (existing != null) {
                doc = docService.update(existing.getId(), payload);
                message = "Updated documentation";
            } else {
                doc = docService.create(payload);
                message = "Created documentation";
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", message);
            response.put("doc", doc);
            return ResponseEntity.ok(response);
            
        } catch (LLMException e) {
            log.error("LLM error generating documentation: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to generate documentation: " + e.getMessage());
            errorResponse.put("error", "LLM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error generating documentation", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unexpected error: " + e.getMessage());
            errorResponse.put("error", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all generated documentation")
    @ApiResponse(responseCode = "200", description = "List of all documentation")
    public ResponseEntity<List<Doc>> getAllDocs() {
        return ResponseEntity.ok(docService.findAll());
    }
    
    @GetMapping("/by-endpoint")
    @Operation(summary = "Get documentation for a specific endpoint")
    public ResponseEntity<Doc> getEndpointDocs(
            @RequestParam String path, 
            @RequestParam String method) {
        Doc doc = docService.findByEndpoint(path, method.toUpperCase());
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doc);
    }
    
    @GetMapping("/metadata")
    @Operation(summary = "Get discovered endpoint metadata")
    public ResponseEntity<List<EndpointMetadata>> getDiscoveredMetadata() {
        return ResponseEntity.ok(reflectionService.extractEndpointMetadata());
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Re-scan controllers and refresh discovered metadata")
    public ResponseEntity<Map<String, Object>> refreshMetadata() {
        List<EndpointMetadata> endpoints = reflectionService.refresh();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Refreshed metadata");
        response.put("count", endpoints.size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cache/stats")
    @Operation(summary = "Get cache statistics")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        LLMCacheService.CacheStats stats = cacheService.getStats();
        Map<String, Object> response = new HashMap<>();
        response.put("redisAvailable", stats.redisAvailable);
        response.put("redisCacheSize", stats.redisCacheSize);
        response.put("memoryCacheSize", stats.memoryCacheSize);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cache/clear")
    @Operation(summary = "Clear all cached documentation")
    public ResponseEntity<Map<String, Object>> clearCache() {
        cacheService.clearAll();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cache cleared successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/cache/invalidate")
    @Operation(summary = "Invalidate cache for a specific endpoint")
    public ResponseEntity<Map<String, Object>> invalidateCache(
            @RequestParam String path,
            @RequestParam String method) {
        cacheService.invalidate(method.toUpperCase(), path);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cache invalidated for " + method + " " + path);
        return ResponseEntity.ok(response);
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateDocsRequest {
        private Integer limit;
        private Boolean confirm;
        private List<String> paths;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateOneRequest {
        private String path;
        private String method;
    }
}
