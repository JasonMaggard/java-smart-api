package com.jasonmaggard.smart_api.api.usage.repository;

import com.jasonmaggard.smart_api.api.usage.entity.ApiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApiUsageLogRepository extends JpaRepository<ApiUsageLog, UUID> {
    
    /**
     * Find all usage logs for a specific endpoint
     */
    List<ApiUsageLog> findByEndpointPathAndHttpMethod(String endpointPath, String httpMethod);
    
    /**
     * Find usage logs within a time range
     */
    List<ApiUsageLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Get the most frequently used endpoints
     */
    @Query("SELECT u.endpointPath, u.httpMethod, COUNT(u) as count " +
           "FROM ApiUsageLog u " +
           "GROUP BY u.endpointPath, u.httpMethod " +
           "ORDER BY COUNT(u) DESC")
    List<Object[]> findTopEndpoints();
    
    /**
     * Get endpoints with slowest average response times
     */
    @Query("SELECT u.endpointPath, u.httpMethod, AVG(u.responseTimeMs) as avgResponseTime " +
           "FROM ApiUsageLog u " +
           "WHERE u.responseTimeMs IS NOT NULL " +
           "GROUP BY u.endpointPath, u.httpMethod " +
           "ORDER BY AVG(u.responseTimeMs) DESC")
    List<Object[]> findSlowestEndpoints();
    
    /**
     * Get total request count
     */
    @Query("SELECT COUNT(u) FROM ApiUsageLog u")
    Long getTotalRequestCount();
    
    /**
     * Get average response time across all endpoints
     */
    @Query("SELECT AVG(u.responseTimeMs) FROM ApiUsageLog u WHERE u.responseTimeMs IS NOT NULL")
    Double getAverageResponseTime();
    
    /**
     * Get usage statistics for a specific endpoint
     */
    @Query("SELECT COUNT(u), AVG(u.responseTimeMs), MIN(u.responseTimeMs), MAX(u.responseTimeMs) " +
           "FROM ApiUsageLog u " +
           "WHERE u.endpointPath = :path AND u.httpMethod = :method")
    Object[] getEndpointStats(@Param("path") String path, @Param("method") String method);
    
    /**
     * Get request count by status code
     */
    @Query("SELECT u.statusCode, COUNT(u) " +
           "FROM ApiUsageLog u " +
           "GROUP BY u.statusCode " +
           "ORDER BY COUNT(u) DESC")
    List<Object[]> getRequestsByStatusCode();
}
