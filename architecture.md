# Architecture

## Overview

Meeting SaaS backend built with **Domain-Driven Design** and a strict **layered architecture**.  
The goal: keep business rules in the domain layer, isolated from infrastructure and framework concerns.

---

## Layered Architecture

```
┌─────────────────────────────────┐
│        Presentation Layer       │  Controllers, DTOs
├─────────────────────────────────┤
│        Application Layer        │  Services, Use Cases, Application Exceptions
├─────────────────────────────────┤
│          Domain Layer           │  Aggregates, Entities, VOs, Domain Exceptions
├─────────────────────────────────┤
│       Infrastructure Layer      │  JPA Repositories, External Integrations
└─────────────────────────────────┘
```

**Dependency rule:** each layer depends only on the layer below. Domain has zero framework dependencies.

---

## Bounded Contexts & Aggregates

| Context | Aggregate Root | Notes |
|---|---|---|
| Identity | `User` | lifecycle, auth, status (AVAILABLE → PLANNING → WORKING) |
| Identity | `Company` | membership management |
| Meeting | `Meeting` | includes `Participant` as internal entity (not standalone AR) |
| Meeting | `VoiceProfile` | speaker identification data |
| Task | `ActionItem` | created by batch; drives the dev workflow |
| Task | `Ticket` | auto-created on ActionItem pickup; GitHub integration |
| Task | `EpicStory` | auto-created by batch; groups ActionItems |

**Cross-BC references:** ID only — no object references across aggregate boundaries.

---

## Key Flows

### Meeting → ActionItem lifecycle
```
[Meeting]
SCHEDULED → IN_PROGRESS → SUBMITTED → BATCH_PROCESSING → COMPLETED

[Batch creates]
EpicStory + ActionItems (status = CANDIDATE)

[ActionItem]
CANDIDATE → PICKED_UP → IN_PROGRESS → DEPLOY_WAITING → QA → RELEASED
```

### User status gates ActionItem pickup
```
AVAILABLE → PLANNING → WORKING → AVAILABLE
```
Only `PLANNING` users can pick up `CANDIDATE` action items.  
Pickup auto-creates a linked `Ticket`.

---

## Domain Exception Strategy

Two exception hierarchies — never mix them:

| Type | Class | When |
|---|---|---|
| Domain | `InvalidStateTransitionException` | illegal status flow |
| Domain | `UnauthorizedActionException` | actor violates business rule |
| Domain | `DuplicateActiveMeetingException` | company-level meeting constraint |
| Application | `ResourceNotFoundException` | entity not found by ID |
| Application | `AlreadyExistsException` | duplicate resource creation |

Domain models throw domain exceptions only.  
Services throw application exceptions and map domain exceptions for the presentation layer.

---

## Value Objects

| VO | Validation |
|---|---|
| `Email` | regex, enforced in constructor |
| `Password` | hashed on creation via `Password.create()` |
| `GitHubRepository` | GitHub repo reference |

---

## Package Structure

```
com.meeting.demo
├── domain
│   ├── model          # Aggregates, Entities, Enums
│   ├── vo             # Value Objects
│   ├── repository     # Repository interfaces (domain-owned)
│   └── exception      # Domain exceptions
├── application
│   ├── service        # Use case orchestration
│   └── exception      # Application exceptions
├── infrastructure
│   └── repository     # JPA implementations
├── presentation
│   ├── controller     # REST controllers
│   └── dto            # Request/Response DTOs
└── config             # Spring configuration
```

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin 1.9.25 |
| Framework | Spring Boot 3.4.5 |
| Persistence | JPA / Hibernate, jOOQ |
| Database | H2 (dev/test), PostgreSQL-compatible |
| Caching | Redis |
| AI | Spring AI + OpenAI |
| Auth | Spring Security + OAuth2 |
| Test | JUnit 5, Mockito-Kotlin |

---

## Testing Strategy

**TDD** — test skeleton is written before implementation.  
Domain logic is unit-tested with no Spring context (pure Kotlin).  
Integration tests use H2 in-memory with `@SpringBootTest`.