# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew clean build        # Build + run all tests
./gradlew test               # Run tests only
./gradlew test --tests "orinnetwork.jpstudy.application.auth.TokenManagerTest"  # Single test class
./gradlew test --tests "*FilterServiceTest"  # Pattern match
java -jar build/libs/app.jar # Run the application (port 8080)
```

Tests use H2 in-memory database (no external services needed). A `TestConfig` class mocks Redis for CI compatibility.

## Architecture

Spring Boot 3.4.9 / Java 21 / Gradle — Japanese language learning platform backend.

### Layered Package Structure (`orinnetwork.jpstudy`)

- **`presentation/`** — REST controllers, JWT filter, scheduled tasks, RabbitMQ consumers
- **`application/`** — Service/use-case classes, DTOs, business logic orchestration
- **`domain/`** — JPA entities, repository interfaces, enums, `BaseEntity` (audit timestamps)
- **`infrastructure/`** — Spring configs (Security, Redis, RabbitMQ, R2, Email), JWT provider, exception handling, external service integrations

Each layer is organized by **feature packages**: `auth`, `exam`, `member`, `post`, `comment`, `progress`, `search`, `notification`, `inquiry`, `image`, `filtering`, `questionbank`. Admin-specific logic lives under `admin/` sub-packages.

### Key Domain Concepts

- **Member** — Abstract entity using Single Table Inheritance (`auth_type` discriminator) with `LocalMember` and `OauthMember` subtypes. Roles: ADMIN, USER.
- **Kanji/Word** — Dictionary entities with soft-delete (`deleted_at`). Words have Meanings, WordTags, and WordKanji (many-to-many).
- **Exam system** — `Exam` → `ExamQuestion` → `Question` (from question bank). `TestAttempt` tracks a member's exam session. `ExamBlueprint` defines exam templates.
- **Progress** — `MemberKanjiProgress`/`MemberWordProgress` with FSRS spaced repetition algorithm.
- **Post/Comment** — Community forum with soft-delete via status enum (ACTIVE/DELETED).

### Patterns & Conventions

- **Static factory methods** — Entities use `@Builder(access = PRIVATE)` + public static `create()` methods. Follow this pattern for new entities.
- **Constructor injection** via Lombok `@RequiredArgsConstructor`
- **`@Transactional(readOnly = true)`** as default on read services; writable for mutations
- **Soft delete** — Use `deleted_at` column, not hard deletes
- **Error handling** — `CustomException` with `ErrorCode` enum (HTTP status + i18n message key). Add new codes to `ErrorCode.java`. Centralized in `GlobalExceptionHandler`.
- **Protected no-args constructor** — `@NoArgsConstructor(access = PROTECTED)` on all entities

### Infrastructure

- **Database**: PostgreSQL (prod) with Flyway migrations (`src/main/resources/db/migration/`). Next migration: check latest V-number and increment.
- **Security**: JWT access (1h) + refresh (6mo) tokens. `JwtAuthenticationFilter` in filter chain. `@AuthenticationPrincipal CustomUserDetails` for current user.
- **Async**: RabbitMQ for inter-service messaging, Spring Events for local async, SSE for real-time notifications with Redis pub/sub.
- **Storage**: Cloudflare R2 via AWS S3 SDK.
- **Email**: Azure Communication Service.

### Git Workflow

- **`dev`** branch for development, PR against **`main`** for production
- Jenkins CI: build → Docker (Kaniko) → push to DockerHub → update K8s manifests
- Commit messages in Korean are common in this repo
