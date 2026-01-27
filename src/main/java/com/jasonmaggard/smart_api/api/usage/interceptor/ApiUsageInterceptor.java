package com.jasonmaggard.smart_api.api.usage.interceptor;

import com.jasonmaggard.smart_api.api.usage.entity.ApiUsageLog;
import com.jasonmaggard.smart_api.api.usage.service.ApiUsageLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiUsageInterceptor implements HandlerInterceptor {
    
    private final ApiUsageLogService usageLogService;
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Record start time
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        try {
            // Calculate response time
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            long responseTimeMs = startTime != null 
                ? System.currentTimeMillis() - startTime 
                : 0L;
            
            // Extract request details
            String endpointPath = request.getRequestURI();
            String httpMethod = request.getMethod();
            int statusCode = response.getStatus();
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            
            // Skip logging for certain endpoints (actuator, swagger, static resources)
            if (shouldSkipLogging(endpointPath)) {
                return;
            }
            
            // Create and save usage log
            ApiUsageLog usageLog = new ApiUsageLog();
            usageLog.setEndpointPath(endpointPath);
            usageLog.setHttpMethod(httpMethod);
            usageLog.setResponseTimeMs((int) responseTimeMs);
            usageLog.setStatusCode(statusCode);
            usageLog.setUserAgent(userAgent);
            usageLog.setIpAddress(ipAddress);
            usageLog.setCreatedAt(LocalDateTime.now());
            
            // Log asynchronously to avoid blocking response
            usageLogService.logApiUsage(usageLog);
            
        } catch (Exception e) {
            log.error("Error logging API usage: {}", e.getMessage());
            // Don't throw - logging failure shouldn't break the API
        }
    }
    
    /**
     * Extract client IP address, handling proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs in X-Forwarded-For
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Determine if we should skip logging for this endpoint
     */
    private boolean shouldSkipLogging(String path) {
        // Skip actuator, swagger UI, static resources, and JobRunr dashboard
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars") ||
               path.startsWith("/static") ||
               path.startsWith("/jobrunr") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".ico") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg");
    }
}
