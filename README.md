# Smart API

Spring Boot REST API with automatic documentation generation and usage tracking.

## Features

- **PostgreSQL** database with Flyway migrations
- **Redis** caching support
- **JPA/Hibernate** for data persistence
- **Swagger/OpenAPI** documentation
- Automatic API usage logging
- AI-powered documentation generation

## Prerequisites

- Java 25
- PostgreSQL
- Redis
- Maven

## Setup

1. Configure database credentials in `.env`:
```env
POSTGRES_HOST=localhost
POSTGRES_USER=smartapi
POSTGRES_PASSWORD=change_me_postgres
POSTGRES_DB=smartapi_db
POSTGRES_PORT=5432
```

2. Start PostgreSQL and Redis services

3. Run the application:
```bash
./mvnw spring-boot:run
```

## API Documentation

Once running, access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Project Structure

```
src/main/java/com/jasonmaggard/smart_api/
├── api/
│   ├── docs/entity/     # Documentation entities
│   ├── post/entity/     # Post entities
│   ├── usage/entity/    # API usage log entities
│   └── user/entity/     # User entities
└── SmartApiApplication.java
```

## Database Migrations

Flyway automatically runs migrations on startup:
- `V1` - Creates users table
- `V2` - Creates posts table
- `V3` - Creates documentation table
- `V4` - Creates API usage logs table
