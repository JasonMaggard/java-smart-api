package com.jasonmaggard.smart_api.api.jobs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jasonmaggard.smart_api.api.docs.dto.EndpointMetadata;
import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import com.jasonmaggard.smart_api.api.docs.service.DocService;
import com.jasonmaggard.smart_api.api.llm.dto.GeneratedDocumentation;
import com.jasonmaggard.smart_api.api.llm.exception.LLMException;
import com.jasonmaggard.smart_api.api.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentationJobService {
    
    private final LLMService llmService;
    private final DocService docService;
    private final ObjectMapper objectMapper;
    
    @Job(name = "Generate Documentation: %0 %1", retries = 3)
    public void generateDocumentation(String method, String path, EndpointMetadata metadata, JobContext jobContext) {
        try {
            jobContext.logger().info(String.format("Starting documentation generation for %s %s", method, path));
            
            // Generate documentation using LLM
            GeneratedDocumentation result = llmService.generateDocumentation(metadata);
            
            // Map result to Doc entity payload using JsonNode
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("endpoint_path", metadata.getFullPath());
            payload.put("http_method", method);
            payload.put("description", result.getDescription());
            payload.set("parameters", objectMapper.valueToTree(result.getParameters()));
            payload.putNull("response_schema");
            payload.set("code_examples", objectMapper.valueToTree(result.getExamples()));
            payload.put("llm_model", result.getModel());
            payload.put("token_count", result.getTokenCount());
            
            // Save or update documentation
            Doc existing = docService.findByEndpoint(metadata.getFullPath(), method);
            
            if (existing != null) {
                docService.update(existing.getId(), payload);
                jobContext.logger().info(String.format("Updated documentation for %s %s", method, path));
            } else {
                docService.create(payload);
                jobContext.logger().info(String.format("Created documentation for %s %s", method, path));
            }
            
            jobContext.saveMetadata("status", "completed");
            jobContext.saveMetadata("endpoint", method + " " + path);
            
        } catch (LLMException e) {
            jobContext.logger().error(String.format("LLM error generating documentation: %s", e.getMessage()));
            jobContext.saveMetadata("error", "LLM_ERROR: " + e.getMessage());
            throw e; // Trigger retry
        } catch (Exception e) {
            jobContext.logger().error(String.format("Unexpected error generating documentation: %s", e.getMessage()));
            jobContext.saveMetadata("error", "INTERNAL_ERROR: " + e.getMessage());
            throw new RuntimeException("Failed to generate documentation: " + e.getMessage(), e);
        }
    }
    
    @Job(name = "Bulk Documentation Generation", retries = 2)
    public void generateBulkDocumentation(int totalEndpoints, JobContext jobContext) {
        jobContext.logger().info(String.format("Starting bulk documentation generation for %d endpoints", totalEndpoints));
        jobContext.saveMetadata("status", "processing");
        jobContext.saveMetadata("totalEndpoints", totalEndpoints);
        // This is just a coordinator job that tracks the bulk operation
    }
}
