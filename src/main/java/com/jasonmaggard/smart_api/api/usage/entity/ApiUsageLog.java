package com.jasonmaggard.smart_api.api.usage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "api_usage_logs",
    indexes = {
        @Index(name = "idx_api_usage_logs_created_at", columnList = "created_at"),
        @Index(name = "idx_api_usage_logs_endpoint_method", columnList = "endpoint_path, http_method")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiUsageLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "endpoint_path", nullable = false, length = 255)
    private String endpointPath;
    
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params_used", columnDefinition = "jsonb")
    private Map<String, Object> paramsUsed;
    
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;
    
    @Column(name = "status_code")
    private Integer statusCode;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
