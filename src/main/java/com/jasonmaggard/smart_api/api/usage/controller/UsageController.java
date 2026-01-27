package com.jasonmaggard.smart_api.api.usage.controller;

import com.jasonmaggard.smart_api.api.usage.dto.EndpointUsageDto;
import com.jasonmaggard.smart_api.api.usage.dto.StatusCodeStatsDto;
import com.jasonmaggard.smart_api.api.usage.dto.UsageStatsDto;
import com.jasonmaggard.smart_api.api.usage.service.ApiUsageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@Tag(name = "Usage Analytics", description = "API usage statistics and analytics")
public class UsageController {
    
    private final ApiUsageLogService usageLogService;
    
    @GetMapping("/stats")
    @Operation(
        summary = "Get overall usage statistics",
        description = "Returns comprehensive statistics including total requests, average response time, success/failure counts"
    )
    public ResponseEntity<UsageStatsDto> getOverallStats() {
        log.info("Fetching overall usage statistics");
        UsageStatsDto stats = usageLogService.getOverallStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/top-endpoints")
    @Operation(
        summary = "Get most frequently used endpoints",
        description = "Returns the top N most frequently called endpoints with their usage statistics"
    )
    public ResponseEntity<List<EndpointUsageDto>> getTopEndpoints(
            @Parameter(description = "Number of top endpoints to return")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Fetching top {} endpoints", limit);
        
        if (limit < 1 || limit > 100) {
            return ResponseEntity.badRequest().build();
        }
        
        List<EndpointUsageDto> topEndpoints = usageLogService.getTopEndpoints(limit);
        return ResponseEntity.ok(topEndpoints);
    }
    
    @GetMapping("/slow-endpoints")
    @Operation(
        summary = "Get slowest endpoints",
        description = "Returns endpoints with the highest average response times"
    )
    public ResponseEntity<List<EndpointUsageDto>> getSlowestEndpoints(
            @Parameter(description = "Number of slow endpoints to return")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Fetching {} slowest endpoints", limit);
        
        if (limit < 1 || limit > 100) {
            return ResponseEntity.badRequest().build();
        }
        
        List<EndpointUsageDto> slowEndpoints = usageLogService.getSlowestEndpoints(limit);
        return ResponseEntity.ok(slowEndpoints);
    }
    
    @GetMapping("/by-endpoint")
    @Operation(
        summary = "Get statistics for a specific endpoint",
        description = "Returns detailed usage statistics for a single endpoint"
    )
    public ResponseEntity<EndpointUsageDto> getEndpointStats(
            @Parameter(description = "Endpoint path (e.g., /api/users)", required = true)
            @RequestParam String path,
            @Parameter(description = "HTTP method (e.g., GET, POST)", required = true)
            @RequestParam String method
    ) {
        log.info("Fetching stats for {} {}", method, path);
        EndpointUsageDto stats = usageLogService.getEndpointStats(path, method.toUpperCase());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/status-codes")
    @Operation(
        summary = "Get request distribution by status code",
        description = "Returns the count and percentage of requests for each HTTP status code"
    )
    public ResponseEntity<List<StatusCodeStatsDto>> getStatusCodeDistribution() {
        log.info("Fetching status code distribution");
        List<StatusCodeStatsDto> distribution = usageLogService.getStatusCodeDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/health")
    @Operation(
        summary = "Usage analytics health check",
        description = "Simple health check for the usage analytics system"
    )
    public ResponseEntity<Map<String, Object>> health() {
        UsageStatsDto stats = usageLogService.getOverallStats();
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "totalRequestsLogged", stats.getTotalRequests(),
            "analyticsAvailable", stats.getTotalRequests() > 0
        ));
    }
}
