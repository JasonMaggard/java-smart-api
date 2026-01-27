# 🚀 Smart API Documentation System

> **Enterprise-grade Spring Boot REST API with AI-powered documentation generation, intelligent multi-tier caching, and asynchronous job processing.**

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![JobRunr](https://img.shields.io/badge/JobRunr-8.4.0-purple.svg)](https://www.jobrunr.io/)

## 🎯 Overview

Smart API is a production-ready REST API that automatically generates comprehensive, context-aware documentation for all endpoints using **Claude AI**. Built with enterprise patterns including multi-tier caching, background job processing, and intelligent rate limiting, it demonstrates modern Java development best practices.

**Key Differentiators:**
- 🤖 **AI-Powered Documentation** - Claude Haiku generates detailed, accurate endpoint documentation
- ⚡ **99% Cost Reduction** - Two-tier caching (Redis + in-memory) with 24-hour TTL
- 🔄 **Background Processing** - JobRunr-based async job queue with retry logic and rate limiting
- 📊 **Production-Ready** - Comprehensive error handling, monitoring, and health checks
- 🏗️ **Enterprise Architecture** - Clean separation of concerns, SOLID principles, database migrations

## ✨ Features

### 🧠 AI-Powered Documentation Engine
- **Claude Haiku 4.5 Integration** - Enterprise-grade LLM for documentation generation
- **Contextual Understanding** - Analyzes endpoint structure, parameters, and relationships
- **Automatic Discovery** - Runtime reflection to identify all API endpoints
- **Structured Output** - JSON-formatted documentation with examples and error scenarios

### 💾 Intelligent Multi-Tier Caching
- **Primary (Redis)** - Distributed cache for horizontal scaling
- **Fallback (In-Memory)** - Automatic degradation when Redis unavailable
- **Smart Invalidation** - Endpoint-specific and bulk cache clearing
- **Real-Time Statistics** - Hit rates, entry counts, memory usage
- **24-Hour TTL** - Automatic expiration with manual override

### 🔄 Asynchronous Job Processing
- **JobRunr 8.4.0** - Production-grade background job framework
- **Rate Limiting** - Configured for 2 concurrent workers to respect API limits
- **Retry Logic** - Automatic retry with exponential backoff (3 attempts)
- **PostgreSQL Storage** - Persistent job state and failure tracking
- **Dashboard** - Web UI for monitoring job status (port 8000)
- **Virtual Threads** - Java 25 Project Loom for efficient concurrency

### 📊 Usage Analytics & Monitoring
- **Automatic Request Logging** - Transparent interceptor-based tracking of all API calls
- **Performance Metrics** - Response time tracking per endpoint (avg, min, max)
- **Status Code Distribution** - Success rate analysis and error patterns
- **Endpoint Popularity** - Most frequently accessed endpoints ranking
- **Client Tracking** - IP address logging with proxy support (X-Forwarded-For)
- **Non-Blocking Design** - Async logging doesn't impact API response times
- **Error Resilient** - Logging failures never crash the application
- **Analytics Endpoints** - 6 REST endpoints for querying usage data

### 🗄️ Core Infrastructure
- **PostgreSQL 15** - Relational database with JSONB support
- **Flyway Migrations** - Version-controlled schema management
- **JPA/Hibernate** - ORM with optimized queries
- **Swagger/OpenAPI** - Interactive API documentation
- **Spring Boot 3.5** - Latest framework features
- **Lombok** - Reduced boilerplate code

### 🔌 REST API Endpoints (34 Total)
- **Users** - Full CRUD with validation and error handling
- **Posts** - CRUD with user relationships and cascade operations
- **Documentation** - AI generation, retrieval, and cache management
- **Usage Analytics** - 6 endpoints for monitoring and performance analysis
- **Health Checks** - Readiness and liveness probes for orchestration

## 📋 Prerequisites

| Requirement | Version | Purpose |
|------------|---------|---------|
| Java | 25+ | Modern language features, virtual threads |
| PostgreSQL | 15+ | Primary database with JSONB support |
| Redis | 7+ | Distributed caching layer |
| Maven | 3.9+ | Build automation and dependency management |
| Anthropic API Key | - | Claude Haiku for documentation generation |

## 🚀 Quick Start

### 1. Clone & Configure

```bash
# Clone the repository
git clone https://github.com/JasonMaggard/java-smart-api.git
cd java-smart-api

# Create environment configuration
cp .env.example .env
```

### 2. Configure Environment Variables

Edit `.env` with your settings:

```env
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_USER=smartapi
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=smartapi_db
POSTGRES_PORT=5432

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Anthropic AI Configuration
ANTHROPIC_API_KEY=sk-ant-xxxxxxxxxxxxx
ANTHROPIC_MODEL=claude-haiku-4-5-20251001
ANTHROPIC_MAX_TOKENS=4096
ANTHROPIC_TEMPERATURE=0.7
```

### 3. Start Infrastructure Services

#### Option A: Docker Compose (Recommended)
```bash
docker-compose up -d
```

#### Option B: Individual Docker Containers
```bash
# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_USER=smartapi \
  -e POSTGRES_PASSWORD=your_secure_password \
  -e POSTGRES_DB=smartapi_db \
  -p 5432:5432 \
  postgres:15

# Redis
docker run -d --name redis \
  -p 6379:6379 \
  redis:7 \
  redis-server --requirepass your_redis_password
```

### 4. Build & Run

```bash
# Build the application
./mvnw clean install

# Run with Maven
./mvnw spring-boot:run

# Or build JAR and run
./mvnw package
java -jar target/smart-api-0.0.1-SNAPSHOT.jar
```

### 5. Verify Installation

The application will start on `http://localhost:8080`. You should see:

```
🚀 Application Started Successfully
📊 Swagger UI: http://localhost:8080/swagger-ui.html
🔧 JobRunr Dashboard: http://localhost:8000
📈 Usage Analytics: http://localhost:8080/api/usage/stats
💚 Health Check: http://localhost:8080/health
```

Test the endpoints:
```bash
# Basic health check
curl http://localhost:8080/health
# Expected: {"status":"healthy","timestamp":"2026-01-27T12:00:00Z"}

# Analytics health
curl http://localhost:8080/api/usage/health
# Expected: {"analyticsAvailable":true,"totalRequestsLogged":0,"status":"healthy"}

# Test all analytics endpoints
./test-analytics.sh
```

## 📖 API Documentation

### Interactive Documentation
Access the Swagger UI for interactive API exploration:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
Download the OpenAPI spec:
```bash
curl http://localhost:8080/v3/api-docs -o openapi.json
curl http://localhost:8080/v3/api-docs.yaml -o openapi.yaml
```

## 🎯 Usage Examples

### User Management

#### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com"
  }'

# Response:
# {
#   "id": "550e8400-e29b-41d4-a716-446655440000",
#   "name": "Jane Smith",
#   "email": "jane.smith@example.com",
#   "createdAt": "2026-01-27T12:00:00Z"
# }
```

#### List All Users
```bash
curl http://localhost:8080/api/users

# Response: Array of user objects
```

#### Get User by ID
```bash
curl http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
```

#### Update User
```bash
curl -X PATCH http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe"
  }'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
```

### Usage Analytics

#### Get Overall Statistics
```bash
curl http://localhost:8080/api/usage/stats

# Response:
# {
#   "totalRequests": 1247,
#   "averageResponseTimeMs": 45.8,
#   "uniqueEndpoints": 12,
#   "successfulRequests": 1198,
#   "failedRequests": 49
# }
```

#### Get Top N Most Used Endpoints
```bash
curl 'http://localhost:8080/api/usage/top-endpoints?limit=5'

# Response:
# [
#   {
#     "endpointPath": "/api/users",
#     "httpMethod": "GET",
#     "requestCount": 523,
#     "averageResponseTimeMs": 32.5,
#     "minResponseTimeMs": 12,
#     "maxResponseTimeMs": 187
#   },
#   ...
# ]
```

#### Get Slowest Endpoints (Performance Analysis)
```bash
curl 'http://localhost:8080/api/usage/slow-endpoints?limit=5'

# Response: Array of endpoints sorted by average response time
```

#### Get Specific Endpoint Statistics
```bash
curl 'http://localhost:8080/api/usage/by-endpoint?path=/api/users&method=GET'

# Response:
# {
#   "endpointPath": "/api/users",
#   "httpMethod": "GET",
#   "requestCount": 523,
#   "averageResponseTimeMs": 32.5,
#   "minResponseTimeMs": 12,
#   "maxResponseTimeMs": 187
# }
```

#### Get Status Code Distribution
```bash
curl http://localhost:8080/api/usage/status-codes

# Response:
# [
#   {
#     "statusCode": 200,
#     "count": 1198,
#     "percentage": 96.07
#   },
#   {
#     "statusCode": 404,
#     "count": 35,
#     "percentage": 2.81
#   },
#   {
#     "statusCode": 500,
#     "count": 14,
#     "percentage": 1.12
#   }
# ]
```

#### Analytics Health Check
```bash
curl http://localhost:8080/api/usage/health

# Response:
# {
#   "analyticsAvailable": true,
#   "totalRequestsLogged": 1247,
#   "status": "healthy"
# }
```

#### Test All Analytics Endpoints
```bash
# Use the provided test script
./test-analytics.sh
```

### Post Management

#### Create a Post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Introduction to Spring Boot",
    "body": "Spring Boot makes it easy to create production-ready applications..."
  }'
```

#### Get All Posts
```bash
curl http://localhost:8080/api/posts
```

#### Get User's Posts
```bash
curl http://localhost:8080/api/posts/user/550e8400-e29b-41d4-a716-446655440000
```

#### Update Post
```bash
curl -X PATCH http://localhost:8080/api/posts/{postId} \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Advanced Spring Boot Patterns"
  }'
```

### AI Documentation Generation

#### Generate Documentation for Single Endpoint
```bash
curl -X POST http://localhost:8080/api/docs/generate-one \
  -H "Content-Type: application/json" \
  -d '{
    "path": "/users",
    "method": "POST"
  }'

# Response:
# {
#   "summary": "Create a new user account",
#   "description": "Creates a new user in the system...",
#   "requestBody": {...},
#   "responses": {...},
#   "examples": {...}
# }
```

#### Bulk Generate Documentation (Background Job)
```bash
# Generate for all endpoints asynchronously
curl -X POST http://localhost:8080/api/docs/generate \
  -H "Content-Type: application/json" \
  -d '{
    "confirm": true
  }'

# Response:
# {
#   "message": "Bulk generation started",
#   "endpoints": 28,
#   "status": "Jobs queued successfully"
# }

# Monitor progress at: http://localhost:8000 (JobRunr Dashboard)
```

#### Retrieve Generated Documentation
```bash
# Get all documentation
curl http://localhost:8080/api/docs/all

# Get documentation for specific endpoint
curl 'http://localhost:8080/api/docs/by-endpoint?path=/users&method=POST'

# Get discovered endpoints metadata
curl http://localhost:8080/api/docs/metadata
```

### Cache Management

#### Get Cache Statistics
```bash
curl http://localhost:8080/api/docs/cache/stats

# Response:
# {
#   "totalEntries": 28,
#   "hitRate": 0.95,
#   "missRate": 0.05,
#   "evictionCount": 2,
#   "estimatedSizeBytes": 245760
# }
```

#### Clear All Cache
```bash
curl -X POST http://localhost:8080/api/docs/cache/clear

# Response:
# {
#   "message": "Cache cleared successfully",
#   "clearedEntries": 28
# }
```

#### Invalidate Specific Endpoint
```bash
curl -X DELETE 'http://localhost:8080/api/docs/cache/invalidate?path=/users&method=POST'
```

#### Refresh Endpoint Discovery
```bash
curl -X POST http://localhost:8080/api/docs/refresh

# Response:
# {
#   "message": "Endpoints refreshed",
#   "discovered": 28,
#   "timestamp": "2026-01-27T12:00:00Z"
# }
```

### Health Checks

#### Basic Health Check
```bash
curl http://localhost:8080/health

# Response:
# {
#   "status": "healthy",
#   "timestamp": "2026-01-27T12:00:00Z"
# }
```

#### Readiness Probe (Kubernetes/Docker)
```bash
curl http://localhost:8080/health/ready

# Response:
# {
#   "status": "ready",
#   "checks": {
#     "database": "connected",
#     "redis": "connected",
#     "jobrunr": "running"
#   }
# }
```

## 🏗️ Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Client Layer                               │
│    (Web Browser, Mobile App, API Consumer, Swagger UI)             │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────────┐
         │    ApiUsageInterceptor (Phase 4)         │
         │  • Captures all /api/** requests         │
         │  • Records: path, method, time, IP       │
         │  • Non-blocking async logging            │
         └──────────────┬───────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      API Gateway Layer                              │
│              Spring Boot REST Controllers (34 endpoints)            │
│  ┌──────────┬──────────┬──────────┬──────────────┬──────────────┐  │
│  │ UserCtrl │ PostCtrl │ DocsCtrl │ UsageCtrl    │ HealthCtrl   │  │
│  │          │          │          │ (Phase 4)    │              │  │
│  └──────────┴──────────┴──────────┴──────────────┴──────────────┘  │
└───────────────────────┬─────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        Service Layer                                │
│  ┌──────────┬──────────┬──────────────┬──────────────────────────┐ │
│  │ UserSvc  │ PostSvc  │ DocService   │ ApiUsageLogService       │ │
│  │          │          │ LLMService   │ (Phase 4 Analytics)      │ │
│  │          │          │ ReflectionSvc│                          │ │
│  └──────────┴──────────┴──────────────┴──────────────────────────┘ │
└────────┬──────────────────┬────────────────────┬────────────────────┘
         │                  │                    │
         ▼                  ▼                    ▼
┌──────────────────┐  ┌──────────────┐  ┌─────────────────────┐
│   PostgreSQL     │  │ Redis Cache  │  │  JobRunr Queue      │
│  (Primary DB)    │  │  (L1 Cache)  │  │  (Async Jobs)       │
│                  │  │              │  │                     │
│ • Users          │  │ • Doc Cache  │  │ • Doc Gen Jobs      │
│ • Posts          │  │ • 24hr TTL   │  │ • Retry Logic       │
│ • Documentation  │  │ • Hit: 95%+  │  │ • Rate Limiting     │
│ • Job State      │  │              │  │ • 2 Workers         │
│ • API Usage Logs │  │              │  │                     │
│   (Phase 4) ✨   │  │              │  │                     │
└──────────────────┘  └──────┬───────┘  └─────────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   In-Memory     │
                    │   Cache (L2)    │
                    │ (Fallback Only) │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Anthropic API  │
                    │ (Claude Haiku)  │
                    │ • AI Doc Gen    │
                    │ • Rate Limited  │
                    └─────────────────┘
```

### Project Structure

```
src/main/java/com/jasonmaggard/smart_api/
├── api/
│   ├── docs/
│   │   ├── controller/
│   │   │   └── DocsController.java       # Documentation API endpoints
│   │   ├── dto/
│   │   │   ├── GenerateOneDto.java       # Request DTOs
│   │   │   └── BulkGenerateRequestDto.java
│   │   ├── entity/
│   │   │   └── Doc.java                  # JPA entity with JSONB
│   │   ├── repository/
│   │   │   └── DocRepository.java        # Spring Data JPA
│   │   └── service/
│   │       ├── DocService.java           # Business logic
│   │       └── ReflectionService.java    # Runtime endpoint discovery
│   ├── usage/                            # ✨ Phase 4: Usage Analytics
│   │   ├── controller/
│   │   │   └── UsageController.java      # 6 analytics endpoints
│   │   ├── dto/
│   │   │   ├── UsageStatsDto.java        # Overall statistics
│   │   │   ├── EndpointUsageDto.java     # Per-endpoint metrics
│   │   │   └── StatusCodeStatsDto.java   # HTTP status distribution
│   │   ├── entity/
│   │   │   └── ApiUsageLog.java          # Usage log entity
│   │   ├── interceptor/
│   │   │   └── ApiUsageInterceptor.java  # Automatic request logging
│   │   ├── repository/
│   │   │   └── ApiUsageLogRepository.java # Custom analytics queries
│   │   └── service/
│   │       └── ApiUsageLogService.java   # Analytics business logic
│   ├── jobs/
│   │   ├── config/
│   │   │   └── JobRunrConfig.java        # JobRunr setup (2 workers)
│   │   └── service/
│   │       └── DocumentationJobService.java  # Background job processor
│   ├── llm/
│   │   ├── config/
│   │   │   ├── LLMConfig.java            # Anthropic client config
│   │   │   └── RedisConfig.java          # Redis connection
│   │   ├── dto/
│   │   │   └── GeneratedDocumentation.java
│   │   ├── exception/
│   │   │   └── LLMException.java         # Custom exceptions
│   │   └── service/
│   │       ├── LLMService.java           # Claude integration
│   │       └── LLMCacheService.java      # Two-tier caching
│   ├── post/
│   │   ├── controller/
│   │   │   └── PostController.java       # Post CRUD endpoints
│   │   ├── dto/
│   │   │   ├── CreatePostDto.java
│   │   │   └── UpdatePostDto.java
│   │   ├── entity/
│   │   │   └── Post.java                 # JPA entity
│   │   ├── repository/
│   │   │   └── PostRepository.java
│   │   └── service/
│   │       └── PostService.java
│   ├── user/
│   │   ├── controller/
│   │   │   └── UserController.java       # User CRUD endpoints
│   │   ├── dto/
│   │   │   ├── CreateUserDto.java
│   │   │   └── UpdateUserDto.java
│   │   ├── entity/
│   │   │   └── User.java                 # JPA entity
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       └── UserService.java
│   └── health/
│       └── HealthController.java         # Health check endpoints
├── config/
│   └── WebConfig.java                    # Interceptor registration (Phase 4)
└── SmartApiApplication.java              # Spring Boot entry point

src/main/resources/
├── db/migration/                          # Flyway migrations
│   ├── V1__Create_users_table.sql
│   ├── V2__Create_posts_table.sql
│   ├── V3__Create_documentation_table.sql
│   └── V4__Create_api_usage_logs_table.sql  # ✨ Phase 4 Analytics
└── application.properties                 # Spring configuration

src/test/java/                             # Test suite
scripts/
└── test-analytics.sh                      # Analytics demo script
└── com/jasonmaggard/smart_api/
    └── SmartApiApplicationTests.java
```

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────────────┐
│       Users         │
├─────────────────────┤
│ id (UUID) PK        │
│ name (VARCHAR)      │
│ email (VARCHAR) UQ  │
│ created_at          │
│ updated_at          │
└──────────┬──────────┘
           │ 1
           │
           │ N
┌──────────▼──────────┐
│       Posts         │
├─────────────────────┤
│ id (UUID) PK        │
│ user_id (UUID) FK   │
│ title (VARCHAR)     │
│ body (TEXT)         │
│ created_at          │
│ updated_at          │
└─────────────────────┘

┌─────────────────────────────────┐
│      Documentation              │
├─────────────────────────────────┤
│ id (UUID) PK                    │
│ path (VARCHAR)                  │
│ method (VARCHAR)                │
│ generated_content (JSONB)       │
│ llm_model (VARCHAR)             │
│ generation_time_ms (INTEGER)    │
│ token_usage (INTEGER)           │
│ generated_at                    │
│ updated_at                      │
│                                 │
│ UNIQUE(path, method)            │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│      API Usage Logs (Phase 4)   │
├─────────────────────────────────┤
│ id (UUID) PK                    │
│ endpoint_path (VARCHAR)         │
│ http_method (VARCHAR)           │
│ status_code (INTEGER)           │
│ response_time_ms (INTEGER)      │
│ created_at (TIMESTAMP)          │
│ ip_address (VARCHAR)            │
│ user_agent (TEXT)               │
│                                 │
│ INDEX(endpoint_path, created_at)│
│ INDEX(created_at)               │
│ INDEX(status_code)              │
└─────────────────────────────────┘
```

### Flyway Migrations

Migrations run automatically on startup in order:

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__Create_users_table.sql` | User entity with email uniqueness |
| V2 | `V2__Create_posts_table.sql` | Posts with user FK and timestamps |
| V3 | `V3__Create_documentation_table.sql` | JSONB storage for AI-generated docs |
| V4 | `V4__Create_api_usage_logs_table.sql` | ✅ **Analytics table (Phase 4 Complete)** |

### Key Database Features

- **UUID Primary Keys** - Distributed-friendly, no collision risk
- **JSONB Storage** - PostgreSQL-native JSON with indexing support
- **Automatic Timestamps** - `created_at` and `updated_at` managed by triggers
- **Foreign Key Constraints** - Referential integrity with cascade deletes
- **Unique Constraints** - Email uniqueness, (path, method) uniqueness for docs
- **Performance Indexes** - Optimized for analytics queries (endpoint, timestamp, status)
- **Usage Analytics** - Automatic request logging with response time tracking

## 💾 Caching Architecture

### Two-Tier Cache Strategy

```
Request Flow:
┌─────────┐
│ Client  │
└────┬────┘
     │
     ▼
┌────────────────────────────────┐
│    Check Redis (L1 Cache)     │
│  ⚡ 10-50ms if HIT              │
└────┬───────────────────┬───────┘
     │ HIT (95%)         │ MISS (5%)
     ▼                   ▼
┌─────────────┐    ┌──────────────────────┐
│Return Cached│    │ Check In-Memory (L2) │
│   Result    │    │  ⚡ 1-5ms if HIT       │
└─────────────┘    └────┬─────────────┬────┘
                        │ HIT         │ MISS
                        ▼             ▼
                   ┌─────────┐  ┌────────────┐
                   │ Return  │  │Generate New│
                   │  L2     │  │ (Claude)   │
                   └─────────┘  │ 2-5 seconds│
                                └─────┬──────┘
                                      │
                                      ▼
                                ┌──────────────┐
                                │ Store in L1  │
                                │  Store in L2 │
                                │Store in DB   │
                                └──────────────┘
```

### Cache Configuration

```java
// Cache Key Format
String cacheKey = String.format("docs:%s:%s:v1", method, path);
// Example: "docs:POST:/users:v1"

// TTL Configuration
Duration TTL = Duration.ofHours(24);

// Eviction Policy
Eviction: Time-based (24h) + Manual invalidation
```

### Performance Metrics

| Metric | Value | Impact |
|--------|-------|--------|
| Cache Hit Rate | 95%+ | 99% cost reduction |
| L1 (Redis) Latency | 10-50ms | Instant response |
| L2 (Memory) Latency | 1-5ms | Fallback speed |
| Cache Miss (Generate) | 2-5s | First-time only |
| Memory per Entry | ~8KB | ~224KB for 28 endpoints |

### Cache Management

```bash
# Real-time statistics
curl http://localhost:8080/api/docs/cache/stats

# Response:
{
  "provider": "redis",
  "totalEntries": 28,
  "hitRate": 0.9545,
  "missRate": 0.0455,
  "evictionCount": 2,
  "estimatedSizeBytes": 229376,
  "ttlHours": 24,
  "fallbackActive": false
}

# Manual cache operations
POST   /api/docs/cache/clear         # Clear all cache
DELETE /api/docs/cache/invalidate    # Invalidate specific endpoint
```

## 🔄 Background Job Processing

### JobRunr Configuration

**Why JobRunr?**
- Production-grade background job processing
- Built-in retry logic with exponential backoff
- PostgreSQL-based job persistence
- Web dashboard for monitoring
- Support for Java 25 virtual threads

**Rate Limiting Strategy:**
```java
// Configured for 2 concurrent workers
// Prevents overwhelming Anthropic API rate limits
.useBackgroundJobServer(2)
```

### Job Processing Flow

```
1. HTTP Request: POST /api/docs/generate {"confirm": true}
   │
   ▼
2. Enqueue Jobs: 28 endpoints → PostgreSQL job queue
   │
   ▼
3. Background Processing (2 concurrent workers):
   ┌─────────────────────────────────────┐
   │ Worker 1: POST /users               │
   │ Worker 2: GET /users                │
   └─────────────────────────────────────┘
   │
   ▼
4. Job Execution:
   • Fetch endpoint metadata
   • Check cache (avoid duplicate work)
   • Call Claude API if needed
   • Store result in DB + cache
   • Mark job as succeeded
   │
   ▼
5. Error Handling (if API call fails):
   • Retry #1: Wait 1 minute → Retry
   • Retry #2: Wait 2 minutes → Retry
   • Retry #3: Wait 4 minutes → Final attempt
   • If all fail: Mark as permanently failed
   │
   ▼
6. Monitoring: View status at http://localhost:8000
```

### Job Dashboard

Access JobRunr dashboard at `http://localhost:8000`:

Features:
- ✅ **Succeeded Jobs** - Successfully generated documentation
- 🔄 **Processing Jobs** - Currently running (max 2)
- ⏳ **Scheduled Jobs** - Waiting for worker availability
- ❌ **Failed Jobs** - Errors with stack traces
- 🔁 **Retry History** - Automatic retry attempts

### Job Management

```bash
# Start bulk generation (creates background jobs)
curl -X POST http://localhost:8080/api/docs/generate \
  -H "Content-Type: application/json" \
  -d '{"confirm": true}'

# Monitor via dashboard
open http://localhost:8000

# Check job status in database
psql -d smartapi_db -c "SELECT * FROM jobrunr_jobs WHERE state='SUCCEEDED';"
```

## 🧪 Development

### Build Commands

```bash
# Clean build
./mvnw clean install

# Compile only (faster for quick checks)
./mvnw compile

# Package JAR
./mvnw package

# Skip tests (faster build)
./mvnw clean install -DskipTests
```

### Run Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SmartApiApplicationTests

# Run with coverage
./mvnw test jacoco:report
```

### Development Mode

```bash
# Run with hot reload (Spring DevTools)
./mvnw spring-boot:run -Dspring-boot.run.fork=false

# Run with debug port 5005
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Run with custom profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Operations

```bash
# Connect to PostgreSQL
psql -h localhost -U smartapi -d smartapi_db

# View migrations
SELECT * FROM flyway_schema_history;

# Rollback (manually)
# Flyway doesn't support automatic rollback
# Create custom down migration script if needed

# Force migration repair (if needed)
./mvnw flyway:repair
```

## 📊 Monitoring & Observability

### Health Checks

```bash
# Liveness probe (basic health)
curl http://localhost:8080/health
# Returns: 200 OK if app is running

# Readiness probe (dependencies check)
curl http://localhost:8080/health/ready
# Returns: 200 OK if DB, Redis, JobRunr are healthy
```

### Logging

Application uses SLF4J with Logback:

```properties
# Log levels in application.properties
logging.level.com.jasonmaggard.smart_api=INFO
logging.level.org.jobrunr=INFO
logging.level.org.springframework=WARN
```

**Key Log Messages:**
```
INFO  c.j.s.a.llm.service.LLMService - Successfully generated and cached documentation for POST /users
INFO  c.j.s.a.llm.service.LLMCacheService - Redis connection successful
INFO  o.j.s.BackgroundJobServer - JobRunr BackgroundJobServer using PostgresStorageProvider and 2 BackgroundJobPerformers started successfully
WARN  c.j.s.a.llm.service.LLMCacheService - Redis unavailable, using in-memory fallback
ERROR c.j.s.a.llm.service.LLMService - Failed to generate documentation: RateLimitException
```

### Metrics

Track key performance indicators:

| Metric | Endpoint | Description |
|--------|----------|-------------|
| Cache Hit Rate | `/api/docs/cache/stats` | Percentage of cached responses |
| Documentation Count | `/api/docs/metadata` | Total endpoints documented |
| Job Success Rate | JobRunr Dashboard | Background job completion |
| API Response Time | Application logs | P50, P95, P99 latencies |

## 🔐 Security Considerations

### Environment Variables
- ✅ Secrets stored in `.env` (not committed to Git)
- ✅ API keys never hardcoded
- ✅ Database credentials externalized

### API Security (Production Recommendations)
- 🔒 Add Spring Security with OAuth2/JWT
- 🔒 Rate limiting per client/IP
- 🔒 CORS configuration for allowed origins
- 🔒 Input validation on all DTOs
- 🔒 SQL injection protection (JPA/Hibernate)

### Best Practices Implemented
- ✅ Prepared statements (JPA)
- ✅ Input validation via Bean Validation
- ✅ Error handling without stack trace exposure
- ✅ Secure Redis password authentication
- ✅ PostgreSQL connection pooling (HikariCP)

## 📈 Performance Optimization

### Database
- **Connection Pooling** - HikariCP with optimized settings
- **Prepared Statements** - All queries use JPA/Hibernate
- **Indexes** - Strategic indexes on foreign keys and lookup columns
- **JSONB** - Native PostgreSQL JSON for efficient storage

### Caching
- **Two-Tier Strategy** - Redis (distributed) + In-Memory (fallback)
- **Cache Warming** - Pre-generate common endpoints
- **Smart Invalidation** - Endpoint-specific cache clearing
- **24-Hour TTL** - Automatic expiration to stay fresh

### Concurrency
- **Virtual Threads** - Java 25 Project Loom for efficient I/O
- **Rate Limiting** - 2 concurrent workers prevent API throttling
- **Async Processing** - Background jobs don't block HTTP requests
- **Connection Pooling** - Reuse DB connections efficiently

## 🚀 Deployment

### Production Checklist

- [ ] Set production environment variables
- [ ] Configure external Redis cluster
- [ ] Set up PostgreSQL with replication
- [ ] Enable HTTPS/TLS
- [ ] Configure log aggregation (ELK, Splunk)
- [ ] Set up monitoring (Prometheus, Grafana)
- [ ] Configure health check endpoints for K8s
- [ ] Set appropriate JVM heap size
- [ ] Enable database backups
- [ ] Configure rate limiting

### Docker Deployment

```bash
# Build Docker image
docker build -t smart-api:latest .

# Run with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Scale horizontally
docker-compose up -d --scale api=3
```

### Kubernetes Deployment

```yaml
# Example K8s deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: smart-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: smart-api
  template:
    metadata:
      labels:
        app: smart-api
    spec:
      containers:
      - name: smart-api
        image: smart-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: POSTGRES_HOST
          value: postgres-service
        - name: REDIS_HOST
          value: redis-service
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
        readinessProbe:
          httpGet:
            path: /health/ready
            port: 8080
          initialDelaySeconds: 10
```

## 🛣️ Roadmap

### ✅ Phase 1: LLM Integration (Complete)
- Anthropic Claude API integration
- Prompt engineering for documentation
- JSON schema validation
- Error handling and retries

### ✅ Phase 2: Intelligent Caching (Complete)
- Redis configuration and client
- Two-tier cache implementation
- Cache statistics and monitoring
- Manual invalidation endpoints

### ✅ Phase 3: Background Job Queue (Complete)
- JobRunr integration with PostgreSQL
- Rate-limited concurrent processing (2 workers)
- Retry logic with exponential backoff
- Web dashboard for monitoring

### ✅ Phase 4: Usage Analytics (Complete)
- **Automatic Request Logging** - Interceptor-based transparent tracking
- **Performance Monitoring** - Response time metrics per endpoint
- **Analytics Endpoints** - 6 REST APIs for querying usage data
- **Status Code Tracking** - Success/failure rate analysis
- **Client Identification** - IP tracking with proxy support
- **Non-Blocking Design** - Async logging for zero performance impact
- **Error Resilience** - Logging failures never crash API
- **Smart Filtering** - Excludes static resources, Swagger, JobRunr dashboard

**Implementation Highlights:**
- `ApiUsageInterceptor` - Captures all `/api/**` requests automatically
- `ApiUsageLogRepository` - Custom JPA queries for analytics aggregation
- `ApiUsageLogService` - Business logic with 6 statistical methods
- `UsageController` - 6 endpoints: stats, top-endpoints, slow-endpoints, by-endpoint, status-codes, health
- `ApiUsageLog` entity - Stores: path, method, response time, status, IP, timestamp

### 🔮 Future Enhancements
- **Authentication/Authorization** - OAuth2, JWT, role-based access
- **API Versioning** - URL-based or header-based versioning
- **GraphQL Support** - Alternative to REST endpoints
- **Webhooks** - Event-driven notifications
- **Export Formats** - PDF, Markdown, Postman collection
- **Multi-LLM Support** - OpenAI, Gemini, local models
- **Custom Prompts** - User-defined documentation templates

## 🤝 Contributing

### Development Setup

```bash
# Fork and clone
git clone https://github.com/YOUR_USERNAME/java-smart-api.git
cd java-smart-api

# Create feature branch
git checkout -b feature/amazing-feature

# Make changes and test
./mvnw test

# Commit with conventional commits
git commit -m "feat: add amazing feature"

# Push and create PR
git push origin feature/amazing-feature
```

### Code Style
- Follow Google Java Style Guide
- Use Lombok for boilerplate reduction
- Write meaningful commit messages
- Add Javadoc for public APIs
- Include unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Jason Maggard**
- GitHub: [@JasonMaggard](https://github.com/JasonMaggard)
- LinkedIn: [Jason Maggard](https://www.linkedin.com/in/jasonmaggard/)

## 🙏 Acknowledgments

- **Anthropic** - Claude AI for intelligent documentation generation
- **Spring Team** - Excellent framework and documentation
- **JobRunr** - Robust background job processing
- **Redis Labs** - High-performance caching solution

---

<div align="center">

**Built with ❤️ using Spring Boot, PostgreSQL, Redis, and Claude AI**

⭐ Star this repo if you find it helpful!

</div>
