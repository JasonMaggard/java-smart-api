package com.jasonmaggard.smart_api.api.llm.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonmaggard.smart_api.api.docs.dto.EndpointMetadata;
import com.jasonmaggard.smart_api.api.llm.config.LLMConfig;
import com.jasonmaggard.smart_api.api.llm.dto.GeneratedDocumentation;
import com.jasonmaggard.smart_api.api.llm.exception.LLMException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {
    
    private final LLMConfig llmConfig;
    private final LLMCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public GeneratedDocumentation generateDocumentation(EndpointMetadata endpoint) {
        String method = endpoint.getMethod();
        String path = endpoint.getFullPath();
        
        // Check cache first
        GeneratedDocumentation cached = cacheService.get(method, path);
        if (cached != null) {
            log.info("Using cached documentation for {} {}", method, path);
            return cached;
        }
        
        log.info("Generating NEW documentation for {} {}", method, path);
        
        try {
            AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(llmConfig.getApiKey())
                .build();
            
            String prompt = buildPrompt(endpoint);
            
            MessageCreateParams params = MessageCreateParams.builder()
                .model(Model.of(llmConfig.getModel()))
                .maxTokens((long) llmConfig.getMaxTokens())
                .temperature(llmConfig.getTemperature())
                .addUserMessage(prompt)
                .build();
            
            Message response = client.messages().create(params);
            
            // Extract text content from response
            String responseText = extractTextContent(response);
            log.debug("LLM Response: {}", responseText);
            
            // Parse the JSON response
            GeneratedDocumentation result = parseResponse(responseText);
            result.setModel(llmConfig.getModel());
            result.setTokenCount((int) response.usage().outputTokens());
            
            // Cache the result
            cacheService.put(method, path, result);
            
            log.info("Successfully generated and cached documentation for {} {}", method, path);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to generate documentation for {} {}: {}", 
                method, path, e.getMessage(), e);
            throw new LLMException("Failed to generate documentation: " + e.getMessage(), e);
        }
    }
    
    private String extractTextContent(Message response) {
        return response.content().stream()
            .filter(block -> block.text().isPresent())
            .findFirst()
            .flatMap(block -> block.text())
            .map(textBlock -> textBlock.text())
            .orElseThrow(() -> new LLMException("No text content in LLM response"));
    }
    
    String buildPrompt(EndpointMetadata endpoint) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an API documentation generator. ");
        prompt.append("Generate comprehensive documentation for the following REST API endpoint.\n\n");
        
        prompt.append("Endpoint Details:\n");
        prompt.append("- HTTP Method: ").append(endpoint.getMethod()).append("\n");
        prompt.append("- Path: ").append(endpoint.getFullPath()).append("\n");
        
        if (endpoint.getParameters() != null && !endpoint.getParameters().isEmpty()) {
            prompt.append("- Parameters: ").append(endpoint.getParameters()).append("\n");
        }
        
        if (endpoint.getReturnType() != null) {
            prompt.append("- Return Type: ").append(endpoint.getReturnType()).append("\n");
        }
        
        prompt.append("\nProvide the documentation in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"description\": \"A clear, concise description of what this endpoint does\",\n");
        prompt.append("  \"parameters\": {\n");
        prompt.append("    \"paramName\": { \"type\": \"string\", \"description\": \"param description\", \"required\": true }\n");
        prompt.append("  },\n");
        prompt.append("  \"examples\": {\n");
        prompt.append("    \"curl\": \"curl example\",\n");
        prompt.append("    \"java\": \"Java example\",\n");
        prompt.append("    \"javascript\": \"JavaScript example\"\n");
        prompt.append("  }\n");
        prompt.append("}\n");
        prompt.append("\nProvide ONLY the JSON object, without any markdown formatting or code blocks.");
        
        return prompt.toString();
    }
    
    GeneratedDocumentation parseResponse(String responseText) {
        try {
            // Clean up potential markdown code blocks
            String cleanedText = responseText.trim();
            if (cleanedText.startsWith("```json")) {
                cleanedText = cleanedText.substring(7);
            } else if (cleanedText.startsWith("```")) {
                cleanedText = cleanedText.substring(3);
            }
            if (cleanedText.endsWith("```")) {
                cleanedText = cleanedText.substring(0, cleanedText.length() - 3);
            }
            cleanedText = cleanedText.trim();
            
            JsonNode root = objectMapper.readTree(cleanedText);
            GeneratedDocumentation doc = new GeneratedDocumentation();

            // description
            doc.setDescription(root.path("description").asText(null));

            // parameters
            Map<String, Object> parameters = objectMapper.convertValue(
                    root.path("parameters"),
                    new TypeReference<Map<String, Object>>() {}
                );
            doc.setParameters(parameters);

            // examples
            Map<String, Object> examples = objectMapper.convertValue(
                    root.path("examples"),
                    new TypeReference<Map<String, Object>>() {}
                );
            doc.setExamples(examples);
            
            return doc;
        } catch (Exception e) {
            log.error("Failed to parse LLM response: {}", responseText, e);
            throw new LLMException("Failed to parse LLM response: " + e.getMessage(), e);
        }
    }
}
