# Smart API

Spring Boot REST API with AI-powered documentation generation, intelligent caching, and comprehensive CRUD operations.

## Features

### Core Infrastructure
- **PostgreSQL** database with Flyway migrations
- **Redis** two-tier caching (distributed + in-memory fallback)
- **JPA/Hibernate** for data persistence
- **Swagger/OpenAPI** interactive API documentation
- **Lombok** for boilerplate reduction

### AI-Powered Documentation
- **Anthropic Claude Integration** - Generates comprehensive API documentation
- **Intelligent Caching** - 24-hour TTL with Redis + in-memory fallback
- **Automatic Reflection** - Discovers endpoints at runtime
- **Cache Management** - Statistics, invalidation, and bulk operations

### REST API Endpoints
- **Users** - Full CRUD operations
- **Posts** - Full CRUD with user relationships
- **Documentation** - AI-generated endpoint documentation
- **Health Checks** - Application health monitoring

## Prerequisites

- Java 25
- PostgreSQL 15+
- Redis 7+
- Maven 3.9+
- Anthropic API Key (for documentation generation)

## Quick Start

### 1. Configure Environment

Create or update `.env` file:
```env
# Database
POSTGRES_HOST=localhost
POSTGRES_USER=smartapi
POSTGRES_PASSWORD=change_me_postgres
POSTGRES_DB=smartapi_db
POSTGRES_PORT=5432

# Redis Cache
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=change_me_redis

# Anthropic AI
ANTHROPIC_API_KEY=your-api-key-here
ANTHROPIC_MODEL=claude-haiku-4-5-20251001
ANTHROPIC_MAX_TOKENS=4096
ANTHROPIC_TEMPERATURE=0.7
```

### 2. Start Services

```bash
# PostgreSQL (if using Docker)
docker run -d --name postgres \
  -e POSTGRES_USER=smartapi \
  -e POSTGRES_PASSWORD=change_me_postgres \
  -e POSTGRES_DB=smartapi_db \
  -p 5432:5432 postgres:15

# Redis (if using Docker)
docker run -d --name redis \
  -p 6379:6379 redis:7
```

### 3. Run Application

```bash
./mvnw spring-boot:run
```

## API Documentation

### Swagger UI
Access interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

### Key Endpoints

#### Users
```bash
# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# List users
curl http://localhost:8080/api/users

# Get user
curl http://localhost:8080/api/users/{id}

# Update user
curl -X PATCH http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/{id}
```

#### Posts
```bash
# Create post
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"userId":"<user-id>","title":"My Post","body":"Post content"}'

# List posts
curl http://localhost:8080/api/posts

# Get post
curl http://localhost:8080/api/posts/{id}

# Get user's posts
curl http://localhost:8080/api/posts/user/{userId}

# Update post
curl -X PATCH http://localhost:8080/api/posts/{id} \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Title"}'

# Delete post
curl -X DELETE http://localhost:8080/api/posts/{id}
```

#### AI Documentation Generation
```bash
# Generate documentation for one endpoint
curl -X POST http://localhost:8080/api/docs/generate-one \
  -H "Content-Type: application/json" \
  -d '{"path":"/users","method":"POST"}'

# Get all documentation
curl http://localhost:8080/api/docs/all

# Get documentation for specific endpoint
curl 'http://localhost:8080/api/docs/by-endpoint?path=/users&method=POST'

# Get discovered endpoints metadata
curl http://localhost:8080/api/docs/metadata

# Refresh endpoint discovery
curl -X POST http://localhost:8080/api/docs/refresh
```

#### Cache Management
```bash
# Get cache statistics
curl http://localhost:8080/api/docs/cache/stats

# Clear all cache
curl -X POST http://localhost:8080/api/docs/cache/clear

# Invalidate specific endpoint cache
curl -X DELETE 'http://localhost:8080/api/docs/cache/invalidate?path=/users&method=POST'
```

#### Health Checks
```bash
# Basic health
curl http://localhost:8080/health

# Readiness check
curl http://localhost:8080/health/ready
```

## Project Structure

```
src/main/java/com/jasonmaggard/smart_api/
├── api/
│   ├── docs/
│   │   ├── controller/      # Documentation API endpoints
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Doc entity
│   │   ├── repository/     # Doc repository
│   │   └── service/        # DocService, ReflectionService
│   ├── llm/
│   │   ├── config/         # LLMConfig, RedisConfig
│   │   ├── dto/           # GeneratedDocumentation
│   │   ├── exception/     # LLMException
│   │   └── service/       # LLMService, LLMCacheService
│   ├── post/
│   │   ├── controller/    # Post CRUD endpoints
│   │   ├── dto/          # CreatePostDto, UpdatePostDto
│   │   ├── entity/       # Post entity
│   │   ├── repository/   # PostRepository
│   │   └── service/      # PostService
│   ├── user/
│   │   ├── controller/   # User CRUD endpoints
│   │   ├── dto/         # CreateUserDto, UpdateUserDto
│   │   ├── entity/      # User entity
│   │   ├── repository/  # UserRepository
│   │   └── service/     # UserService
│   └── health/          # Health check endpoints
└── SmartApiApplication.java

src/main/resources/
├── db/migration/         # Flyway migration scripts
│   ├── V1__Create_users_table.sql
│   ├── V2__Create_posts_table.sql
│   ├── V3__Create_documentation_table.sql
│   └── V4__Create_api_usage_logs_table.sql
└── application.properties
```

## Database Schema

### Migrations
Flyway automatically runs migrations on startup:
- **V1** - Users table with email unique constraint
- **V2** - Posts table with user foreign key and timestamps
- **V3** - Documentation table with JSONB fields and LLM metadata
- **V4** - API usage logs table for analytics (ready for Phase 4)

## Caching Architecture

### Two-Tier Cache System
1. **Primary (Redis)**
   - Distributed cache shared across instances
   - Survives application restarts
   - 24-hour TTL

2. **Fallback (In-Memory)**
   - Automatic when Redis unavailable
   - ConcurrentHashMap implementation
   - Manual expiration checking

### Cache Key Format
```
docs:{METHOD}:{PATH}:v1
Example: docs:POST:/users:v1
```

### Performance Impact
- **Cache Hit**: ~10-50ms (99% cost reduction)
- **Cache Miss**: ~2-5 seconds + Claude API cost
- **Typical Savings**: 99% reduction in API costs after first generation

## Development

### Build
```bash
./mvnw clean install
```

### Run Tests
```bash
./mvnw test
```

### Compile Only
```bash
./mvnw compile
```

## Configuration

### Application Properties
Key configuration in `application.properties`:
- Database connection (PostgreSQL)
- Redis connection
- Anthropic API settings
- Flyway migration settings

### Environment Variables
All sensitive configuration uses environment variables from `.env`:
- Database credentials
- Redis password
- Anthropic API key
- Model parameters

## Features Roadmap

### ✅ Phase 1: LLM Integration
- Anthropic Claude API integration
- Documentation generation
- Prompt engineering
- JSON parsing

### ✅ Phase 2: Caching (Current)
- Redis configuration
- Two-tier cache implementation
- Cache management endpoints
- Statistics and monitoring

### 🚧 Phase 3: Queue System (Next)
- Background job processing
- Bulk documentation generation
- Retry logic
- Status tracking

### 📋 Phase 4: Usage Analytics
- API usage tracking
- Analytics endpoints
- Response time monitoring
- Most-used endpoints dashboard

## Monitoring

### Log Levels
- **INFO**: Cache hits/misses, API operations, service status
- **DEBUG**: Detailed cache operations, LLM responses
- **WARN**: Redis failures, fallback activation
- **ERROR**: API failures, cache errors, unexpected issues

### Key Metrics
- Cache hit rate (check via `/api/docs/cache/stats`)
- Documentation generation count
- API response times
- LLM token usage
