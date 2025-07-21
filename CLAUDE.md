# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application built with Kotlin that implements a meeting analysis system with AI capabilities. The application uses a layered architecture and integrates with OpenAI for chat and analysis features.

## Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run the application
./gradlew bootRun

# Run with Docker Compose (includes Redis)
docker-compose -f compose.yaml up -d
```

### Database Access
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

## Architecture

### Layered Architecture
The application follows a clean layered architecture pattern:

1. **Presentation Layer** (`src/main/kotlin/com/meeting/demo/presentation/`)
   - Controllers handle HTTP requests/responses
   - DTOs for data transfer between layers
   - Controllers: `ChatController`, `UserController`, `PageController`

2. **Domain Layer** (`src/main/kotlin/com/meeting/demo/domain/`)
   - Core business entities and logic
   - Repository interfaces (not implementations)
   - Domain services
   - Models: `User`, `Company`, `Participant`, `VoiceProfile`

3. **Infrastructure Layer** (`src/main/kotlin/com/meeting/demo/infrastructure/`)
   - Repository implementations
   - External service integrations
   - Currently uses in-memory repositories

4. **Application Services** (`src/main/kotlin/com/meeting/demo/service/`)
   - Business logic coordination
   - `ChatService` for AI interactions
   - `UserService` for user management

### Key Components

**AI Integration:**
- Spring AI with OpenAI integration configured in `AiConfig.kt`
- `ChatService` provides AI chat, transcription summarization, and analysis
- API endpoints in `ChatController` for different AI operations

**Meeting Analysis Models:**
- `SpeakerSegment`: Voice segments with speaker identification
- `MeetingAnalysis`: Analysis results with speaker insights
- `VoiceProfile`: Speaker voice characteristics

**Configuration:**
- `application.yaml`: Main configuration with database, Redis, AI settings
- `SecurityConfig.kt`: Security configuration
- `compose.yaml`: Redis service for caching

## Domain Requirements

Refer to `src/main/resources/static/domian-definition.md` for complete domain specifications including:
- User management with company roles
- Company management
- Ticket system with GitHub integration
- Meeting management
- Action items linked to meetings and tickets

## Technology Stack

- **Framework:** Spring Boot 3.4.5 with Kotlin 1.9.25
- **Database:** H2 (in-memory), JPA/Hibernate, jOOQ
- **Caching:** Redis
- **AI:** Spring AI with OpenAI
- **Security:** Spring Security with OAuth2
- **Template Engine:** Thymeleaf
- **Build Tool:** Gradle

## Environment Variables

Required environment variables:
- `OPENAI_API_KEY`: OpenAI API key for AI features

## CI/CD

GitHub Actions workflow (`.github/workflows/main.yml`) includes:
- Build and test on JDK 17
- Docker Hub integration
- Deployment with Docker Compose
- Secrets management for API keys and credentials