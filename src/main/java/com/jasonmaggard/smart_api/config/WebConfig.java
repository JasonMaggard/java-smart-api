package com.jasonmaggard.smart_api.config;

import com.jasonmaggard.smart_api.api.usage.interceptor.ApiUsageInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final ApiUsageInterceptor apiUsageInterceptor;
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        HandlerInterceptor interceptor = Objects.requireNonNull(apiUsageInterceptor, 
            "ApiUsageInterceptor cannot be null");
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**")  // Track all API endpoints
                .excludePathPatterns(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**",
                    "/jobrunr/**"
                );
    }
}
