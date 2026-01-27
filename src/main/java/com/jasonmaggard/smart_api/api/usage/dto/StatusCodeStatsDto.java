package com.jasonmaggard.smart_api.api.usage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCodeStatsDto {
    private Integer statusCode;
    private Long count;
    private Double percentage;
}
