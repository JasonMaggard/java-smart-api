package com.jasonmaggard.smart_api.api.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "anthropic")
@Data
public class LLMConfig {
    
    private String apiKey;
    private String model = "claude-3-5-sonnet-20241022";
    private Integer maxTokens = 4096;
    private Double temperature = 0.7;
}
