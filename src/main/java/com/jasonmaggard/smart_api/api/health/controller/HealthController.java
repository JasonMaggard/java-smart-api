package com.jasonmaggard.smart_api.api.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {
    
    @GetMapping
    @Operation(summary = "Health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<HealthResponse> check() {
        return ResponseEntity.ok(new HealthResponse("healthy", new Date()));
    }
    
    @GetMapping("/ready")
    @Operation(summary = "Readiness check")
    @ApiResponse(responseCode = "200", description = "Service is ready")
    public ResponseEntity<ReadinessResponse> ready() {
        return ResponseEntity.ok(new ReadinessResponse("ready", true));
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthResponse {
        private String status;
        private Date timestamp;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadinessResponse {
        private String status;
        private boolean ready;
    }
}
