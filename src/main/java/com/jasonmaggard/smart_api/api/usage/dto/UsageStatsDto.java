package com.jasonmaggard.smart_api.api.usage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsDto {
    private Long totalRequests;
    private Double averageResponseTimeMs;
    private Integer uniqueEndpoints;
    private Long successfulRequests;
    private Long failedRequests;
}
