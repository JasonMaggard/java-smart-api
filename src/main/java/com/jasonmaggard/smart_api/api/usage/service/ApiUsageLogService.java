package com.jasonmaggard.smart_api.api.usage.service;

import com.jasonmaggard.smart_api.api.usage.dto.EndpointUsageDto;
import com.jasonmaggard.smart_api.api.usage.dto.StatusCodeStatsDto;
import com.jasonmaggard.smart_api.api.usage.dto.UsageStatsDto;
import com.jasonmaggard.smart_api.api.usage.entity.ApiUsageLog;
import com.jasonmaggard.smart_api.api.usage.repository.ApiUsageLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiUsageLogService {
    
    private final ApiUsageLogRepository repository;
    
    /**
     * Log an API request
     */
    @Transactional
    public void logApiUsage(ApiUsageLog usageLog) {
        try {
            repository.save(usageLog);
            log.debug("Logged API usage: {} {} - {}ms", 
                usageLog.getHttpMethod(), 
                usageLog.getEndpointPath(), 
                usageLog.getResponseTimeMs());
        } catch (Exception e) {
            log.error("Failed to log API usage: {}", e.getMessage());
            // Don't throw exception - logging failure shouldn't break the API
        }
    }
    
    /**
     * Get overall usage statistics
     */
    @Transactional(readOnly = true)
    public UsageStatsDto getOverallStats() {
        Long totalRequests = repository.getTotalRequestCount();
        Double avgResponseTime = repository.getAverageResponseTime();
        
        List<Object[]> statusCodes = repository.getRequestsByStatusCode();
        long successfulRequests = statusCodes.stream()
            .filter(row -> {
                Integer statusCode = (Integer) row[0];
                return statusCode != null && statusCode >= 200 && statusCode < 300;
            })
            .mapToLong(row -> ((Number) row[1]).longValue())
            .sum();
        
        long failedRequests = totalRequests - successfulRequests;
        
        // Count unique endpoints
        List<Object[]> topEndpoints = repository.findTopEndpoints();
        int uniqueEndpoints = topEndpoints.size();
        
        return new UsageStatsDto(
            totalRequests,
            avgResponseTime != null ? avgResponseTime : 0.0,
            uniqueEndpoints,
            successfulRequests,
            failedRequests
        );
    }
    
    /**
     * Get top N most frequently used endpoints
     */
    @Transactional(readOnly = true)
    public List<EndpointUsageDto> getTopEndpoints(int limit) {
        List<Object[]> results = repository.findTopEndpoints();
        
        return results.stream()
            .limit(limit)
            .map(row -> {
                String path = (String) row[0];
                String method = (String) row[1];
                Long count = ((Number) row[2]).longValue();
                
                // Get detailed stats for this endpoint
                Object[] stats = repository.getEndpointStats(path, method);
                if (stats == null || stats.length < 4) {
                    return new EndpointUsageDto(path, method, count, 0.0, 0L, 0L);
                }
                
                Double avgTime = stats[1] != null ? ((Number) stats[1]).doubleValue() : 0.0;
                Long minTime = stats[2] != null ? ((Number) stats[2]).longValue() : 0L;
                Long maxTime = stats[3] != null ? ((Number) stats[3]).longValue() : 0L;
                
                return new EndpointUsageDto(path, method, count, avgTime, minTime, maxTime);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get slowest endpoints by average response time
     */
    @Transactional(readOnly = true)
    public List<EndpointUsageDto> getSlowestEndpoints(int limit) {
        List<Object[]> results = repository.findSlowestEndpoints();
        
        return results.stream()
            .limit(limit)
            .map(row -> {
                String path = (String) row[0];
                String method = (String) row[1];
                Double avgTime = ((Number) row[2]).doubleValue();
                
                // Get full stats for this endpoint
                Object[] stats = repository.getEndpointStats(path, method);
                if (stats == null || stats.length < 4) {
                    return new EndpointUsageDto(path, method, 0L, avgTime, 0L, 0L);
                }
                
                Long count = ((Number) stats[0]).longValue();
                Long minTime = stats[2] != null ? ((Number) stats[2]).longValue() : 0L;
                Long maxTime = stats[3] != null ? ((Number) stats[3]).longValue() : 0L;
                
                return new EndpointUsageDto(path, method, count, avgTime, minTime, maxTime);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get statistics for a specific endpoint
     */
    @Transactional(readOnly = true)
    public EndpointUsageDto getEndpointStats(String path, String method) {
        Object[] stats = repository.getEndpointStats(path, method);
        
        if (stats == null || stats.length < 4 || stats[0] == null) {
            return new EndpointUsageDto(path, method, 0L, 0.0, 0L, 0L);
        }
        
        try {
            Long count = stats[0] instanceof Number ? ((Number) stats[0]).longValue() : 0L;
            Double avgTime = stats[1] instanceof Number ? ((Number) stats[1]).doubleValue() : 0.0;
            Long minTime = stats[2] instanceof Number ? ((Number) stats[2]).longValue() : 0L;
            Long maxTime = stats[3] instanceof Number ? ((Number) stats[3]).longValue() : 0L;
            
            return new EndpointUsageDto(path, method, count, avgTime, minTime, maxTime);
        } catch (Exception e) {
            log.error("Error parsing endpoint stats: {}", e.getMessage());
            return new EndpointUsageDto(path, method, 0L, 0.0, 0L, 0L);
        }
    }
    
    /**
     * Get request distribution by status code
     */
    @Transactional(readOnly = true)
    public List<StatusCodeStatsDto> getStatusCodeDistribution() {
        List<Object[]> results = repository.getRequestsByStatusCode();
        Long totalRequests = repository.getTotalRequestCount();
        
        return results.stream()
            .map(row -> {
                Integer statusCode = (Integer) row[0];
                Long count = ((Number) row[1]).longValue();
                Double percentage = totalRequests > 0 
                    ? (count.doubleValue() / totalRequests.doubleValue()) * 100 
                    : 0.0;
                
                return new StatusCodeStatsDto(statusCode, count, percentage);
            })
            .collect(Collectors.toList());
    }
}
