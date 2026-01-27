package com.jasonmaggard.smart_api.api.llm.service;

import com.jasonmaggard.smart_api.api.llm.dto.GeneratedDocumentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LLMCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // In-memory fallback cache
    private final Map<String, CachedDocument> memoryCache = new ConcurrentHashMap<>();
    
    // Cache TTL: 24 hours
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static final String CACHE_KEY_PREFIX = "docs:";
    private static final String CACHE_VERSION = "v1";
    
    private boolean redisAvailable = true;
    
    public LLMCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        testRedisConnection();
    }
    
    /**
     * Generate cache key for endpoint documentation
     * Format: docs:{METHOD}:{PATH}:v1
     */
    public String generateCacheKey(String method, String path) {
        return CACHE_KEY_PREFIX + method + ":" + path + ":" + CACHE_VERSION;
    }
    
    /**
     * Get cached documentation for an endpoint
     */
    public GeneratedDocumentation get(String method, String path) {
        String cacheKey = generateCacheKey(method, path);
        
        // Try Redis first if available
        if (redisAvailable) {
            try {
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.debug("Cache HIT (Redis): {} {}", method, path);
                    return (GeneratedDocumentation) cached;
                }
            } catch (RedisConnectionFailureException e) {
                log.warn("Redis connection failed, falling back to memory cache: {}", e.getMessage());
                redisAvailable = false;
            } catch (Exception e) {
                log.error("Error retrieving from Redis cache: {}", e.getMessage());
            }
        }
        
        // Fallback to memory cache
        CachedDocument memCached = memoryCache.get(cacheKey);
        if (memCached != null && !memCached.isExpired()) {
            log.debug("Cache HIT (Memory): {} {}", method, path);
            return memCached.documentation;
        }
        
        if (memCached != null && memCached.isExpired()) {
            memoryCache.remove(cacheKey);
        }
        
        log.debug("Cache MISS: {} {}", method, path);
        return null;
    }
    
    /**
     * Store documentation in cache
     */
    public void put(String method, String path, GeneratedDocumentation documentation) {
        String cacheKey = generateCacheKey(method, path);
        
        // Store in Redis if available
        if (redisAvailable) {
            try {
                redisTemplate.opsForValue().set(cacheKey, documentation, CACHE_TTL);
                log.debug("Cached in Redis: {} {}", method, path);
            } catch (RedisConnectionFailureException e) {
                log.warn("Redis connection failed, using memory cache only: {}", e.getMessage());
                redisAvailable = false;
            } catch (Exception e) {
                log.error("Error storing in Redis cache: {}", e.getMessage());
            }
        }
        
        // Always store in memory cache as fallback
        memoryCache.put(cacheKey, new CachedDocument(documentation, System.currentTimeMillis()));
        log.debug("Cached in Memory: {} {}", method, path);
    }
    
    /**
     * Invalidate cached documentation for an endpoint
     */
    public void invalidate(String method, String path) {
        String cacheKey = generateCacheKey(method, path);
        
        // Remove from Redis if available
        if (redisAvailable) {
            try {
                redisTemplate.delete(cacheKey);
                log.debug("Invalidated Redis cache: {} {}", method, path);
            } catch (Exception e) {
                log.error("Error invalidating Redis cache: {}", e.getMessage());
            }
        }
        
        // Remove from memory cache
        memoryCache.remove(cacheKey);
        log.debug("Invalidated Memory cache: {} {}", method, path);
    }
    
    /**
     * Clear all cached documentation
     */
    public void clearAll() {
        // Clear Redis cache
        if (redisAvailable) {
            try {
                var keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    log.info("Cleared {} entries from Redis cache", keys.size());
                }
            } catch (Exception e) {
                log.error("Error clearing Redis cache: {}", e.getMessage());
            }
        }
        
        // Clear memory cache
        int memSize = memoryCache.size();
        memoryCache.clear();
        log.info("Cleared {} entries from Memory cache", memSize);
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        CacheStats stats = new CacheStats();
        stats.redisAvailable = redisAvailable;
        stats.memoryCacheSize = memoryCache.size();
        
        if (redisAvailable) {
            try {
                var keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
                stats.redisCacheSize = keys != null ? keys.size() : 0;
            } catch (Exception e) {
                log.error("Error getting Redis cache size: {}", e.getMessage());
            }
        }
        
        return stats;
    }
    
    private void testRedisConnection() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("Redis connection successful");
            redisAvailable = true;
        } catch (Exception e) {
            log.warn("Redis connection failed, will use memory cache only: {}", e.getMessage());
            redisAvailable = false;
        }
    }
    
    /**
     * Internal class to hold cached documents with expiration time
     */
    private static class CachedDocument {
        final GeneratedDocumentation documentation;
        final long timestamp;
        
        CachedDocument(GeneratedDocumentation documentation, long timestamp) {
            this.documentation = documentation;
            this.timestamp = timestamp;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL.toMillis();
        }
    }
    
    /**
     * Cache statistics
     */
    public static class CacheStats {
        public boolean redisAvailable;
        public int redisCacheSize;
        public int memoryCacheSize;
    }
}
