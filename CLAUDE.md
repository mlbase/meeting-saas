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

Full spec: `src/main/resources/static/domian-definition.md`

Bounded Contexts: **Identity** (User, Company) / **Meeting** (Meeting, VoiceProfile) / **Task** (ActionItem, Ticket, EpicStory)  
Cross-BC references: ID only, no object references.

### Key flows (already agreed)
- Meeting: `SCHEDULED → IN_PROGRESS → SUBMITTED → BATCH_PROCESSING → COMPLETED` (C-level only creates)
- ActionItem: `CANDIDATE → PICKED_UP → IN_PROGRESS → DEPLOY_WAITING → QA → RELEASED`
- UserStatus: `AVAILABLE → PLANNING → WORKING → AVAILABLE`

## Exception Strategy

- `domain/exception/DomainException.kt` — business rule violations (`InvalidStateTransitionException`, `UnauthorizedActionException`, `DuplicateActiveMeetingException`)
- `application/exception/ApplicationException.kt` — infra/use-case failures (`ResourceNotFoundException`, `AlreadyExistsException`)
- Domain models throw domain exceptions only. Services throw application exceptions.

## Development Style

**TDD** — test skeleton first, then implement. Never write business logic without a failing test.

## Work In Progress

See `planning.md` for priority order and task checklist. Always follow it top to bottom.

### What's done
- Domain definition finalized (`domian-definition.md`)
- Test skeletons written: `UserTest`, `UserStatusTest`, `MeetingTest`, `MeetingDomainServiceTest`, `ActionItemTest`

### What's incomplete (needs implementation)
- `ActionItem.kt`: wrong status enum (`PENDING/CANDIDATE/CONFIRMED/REJECTED` → should be `CANDIDATE/PICKED_UP/IN_PROGRESS/DEPLOY_WAITING/QA/RELEASED`), broken `pickUp` signature, missing methods (`startProgress`, `submitForDeploy`, `completeDeploy`, `completeQA`)
- `User.kt`: missing `status: UserStatus` field (needed for `pickUp` validation)
- `ActionItemTest.kt` line 58: `user=` arg incomplete — waiting on above fixes

### Current task
Implement `ActionItem.kt` domain logic to match test skeletons.

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