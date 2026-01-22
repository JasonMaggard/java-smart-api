package com.jasonmaggard.smart_api.api.docs.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "documentation",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_documentation_endpoint_method",
        columnNames = {"endpoint_path", "http_method"}
    ),
    indexes = @Index(
        name = "idx_documentation_endpoint_method",
        columnList = "endpoint_path, http_method"
    )
)
public class Doc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "endpoint_path", nullable = false, length = 255)
    private String endpointPath;
    
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> parameters;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_schema", columnDefinition = "jsonb")
    private Map<String, Object> responseSchema;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "code_examples", columnDefinition = "jsonb")
    private Map<String, Object> codeExamples;
    
    @Column(name = "llm_model", length = 50)
    private String llmModel;
    
    @Column(name = "token_count")
    private Integer tokenCount;
    
    @Column(name = "generated_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date generatedAt;
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
