package com.jasonmaggard.smart_api.api.docs.controller;

import com.jasonmaggard.smart_api.api.docs.dto.EndpointMetadata;
import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import com.jasonmaggard.smart_api.api.docs.service.DocService;
import com.jasonmaggard.smart_api.api.docs.service.ReflectionService;
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
        
        // Find endpoint metadata
        EndpointMetadata metadata = reflectionService.extractEndpointMetadata().stream()
            .filter(e -> e.getPath().equals(path) && e.getMethod().equals(method))
            .findFirst()
            .orElse(null);
        
        // In a real implementation, call LLM service to generate documentation
        // For now, create a placeholder
        Map<String, Object> payload = new HashMap<>();
        payload.put("endpoint_path", metadata != null ? metadata.getFullPath() : path);
        payload.put("http_method", method);
        payload.put("description", "Generated documentation for " + path);
        payload.put("parameters", metadata != null ? metadata.getParameters() : null);
        
        Doc existing = docService.findByEndpoint(
            metadata != null ? metadata.getFullPath() : path, 
            method
        );
        
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
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all generated documentation")
    @ApiResponse(responseCode = "200", description = "List of all documentation")
    public ResponseEntity<List<Doc>> getAllDocs() {
        return ResponseEntity.ok(docService.findAll());
    }
    
    @GetMapping("/{path}/{method}")
    @Operation(summary = "Get documentation for a specific endpoint")
    public ResponseEntity<Doc> getEndpointDocs(
            @PathVariable String path, 
            @PathVariable String method) {
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
