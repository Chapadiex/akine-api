# GEMINI.md - AKINE Backend

## Purpose

This file defines the backend-specific operating rules for Gemini inside:

`akine-api/`

It complements the root-level workspace instructions.

If there is any conflict:
1. security, privacy, and product rules from the root instructions always win
2. this file wins for backend-local implementation decisions

---

## Scope

These instructions apply only to the backend project and govern:

- Java code
- Spring Boot configuration
- REST API design
- services and business rules
- persistence and repositories
- Flyway migrations
- validation
- backend security
- backend testing
- backend verification

---

## Project context

This backend belongs to a multi-tenant healthcare SaaS platform.

Backend priorities:
- tenant isolation
- data integrity
- explicit contracts
- predictable behavior
- operational safety
- minimal regression risk

Do not optimize for cleverness.  
Optimize for safe, traceable, and verifiable delivery.

---

## Stack

Backend stack:
- Java 21
- Spring Boot
- Maven
- PostgreSQL 15
- Flyway
- MapStruct
- REST API
- JWT-based security model

General backend principles:
- clarity over magic
- explicit contracts over implicit behavior
- service-driven business rules
- safe multi-tenant isolation
- small, verifiable changes

---

## Core behavior required from Gemini

Before changing backend code, always:

1. Restate the backend goal briefly.
2. Identify assumptions, risks, blockers, and cross-module impact.
3. Propose a short implementation plan.
4. Mention the likely packages/files to touch.
5. Prefer the smallest safe implementation.
6. Only then implement.

If missing information materially affects:
- security
- data integrity
- API contracts
- tenant isolation
- permissions
- database behavior

stop and request clarification before implementing.

Never jump directly into code for sensitive backend changes.

---

## Safe change policy

Default mode is **safe edit**, not speculative rewrite.

Always prefer:
- minimal, targeted changes
- preserving working architecture
- keeping diff scope small
- respecting existing package boundaries
- updating only what the change really requires

Do not:
- refactor unrelated modules
- redesign security by intuition
- silently change contracts
- introduce heavy dependencies without strong justification
- rewrite existing structure just because a “cleaner” version is possible
- hide breaking changes behind partial compatibility tricks

If the change is risky:
- state the risk
- propose a safer path
- keep behavior stable unless explicit redesign was requested

---

## Expected package structure

Expected backend structure:

`src/main/java/.../`
- `config/`
- `controller/`
- `service/`
- `repository/`
- `mapper/`
- `dto/`
- `domain/`
- `exception/`

Preferred responsibility by package:
- `config/` -> framework, security, CORS, application configuration
- `controller/` -> request/response orchestration only
- `service/` -> business rules and transactional use cases
- `repository/` -> persistence access
- `mapper/` -> MapStruct conversions
- `dto/` -> request/response contracts
- `domain/` -> entities and value objects
- `exception/` -> global error handling and custom exceptions

Do not blur these boundaries without explicit reason.

---

## Architecture rules

### Controllers

Controllers must:
- orchestrate HTTP request and response only
- use DTOs for input and output
- validate input with `@Valid` when applicable
- delegate business behavior to services
- avoid persistence logic
- avoid business rules
- avoid infrastructure leakage in responses

Controllers must not:
- contain business logic
- access repositories directly unless explicitly justified and very limited
- return JPA entities as public API responses
- implement authorization shortcuts by assumption

### Services

Services must:
- contain business rules and use-case orchestration
- define transactional boundaries with `@Transactional` where appropriate
- enforce domain behavior explicitly
- keep HTTP concerns out of service logic
- coordinate repositories, mappers, and validators cleanly

Services must not:
- depend on controller-layer concepts
- hide critical side effects
- silently bypass validation or permission checks

### Repositories

Repositories must:
- focus on persistence access
- keep queries clear and reviewable
- avoid N+1 problems
- use pagination for large result sets
- be reviewed carefully when adding non-trivial joins or filters

Repositories must not:
- contain business policy
- become a dumping ground for workflow logic
- return more data than needed for the use case

### Domain / Entities

Entities and domain objects:
- must not be exposed directly through public API contracts
- may contain simple local invariants
- must not be tightly coupled to HTTP or external transport concerns
- should preserve data integrity and consistency

Avoid putting application workflow logic inside entities unless that rule clearly belongs to the domain model.

---

## DTO and mapping rules

DTOs are mandatory for API boundaries.

Rules:
- use request DTOs for input
- use response DTOs for output
- never expose entities directly from controllers
- use MapStruct for DTO ↔ Entity conversion
- prefer explicit mappings over implicit surprises
- recommended: `unmappedTargetPolicy = ReportingPolicy.ERROR`
- if a contract changes, update tests and documentation
- if a field has business meaning, do not hide default behavior inside mappers without documenting it

When modifying a contract, review all affected layers:
- controller
- service
- mapper
- dto
- frontend consumers
- validation
- docs
- tests

Do not change API behavior silently.

---

## Validation rules

Validate as early as possible at the correct boundary.

Rules:
- validate request payloads at controller boundary
- enforce business validation in service layer
- do not rely only on database exceptions for predictable validation cases
- return friendly and consistent validation errors
- make validation rules explicit and testable

Do not:
- accept invalid data and “fix it later”
- mix transport validation with business rule validation without clarity
- return vague validation failures when a precise error is possible

---

## Security rules

This backend handles sensitive healthcare and personal data.

Mandatory:
- validate authentication and authorization on every sensitive operation
- enforce consultorio isolation in every read/write
- prevent cross-consultorio access
- prevent IDOR-style access patterns
- validate ownership/visibility server-side
- treat frontend checks as UX only, never as security

Never:
- bypass tenant checks for convenience
- trust IDs sent by the client without authorization validation
- expose sensitive information in error messages
- log PII or clinical content
- weaken security configuration “just for testing” unless explicitly requested and clearly isolated

If JWT/security is still evolving:
- do not redesign it implicitly
- do not loosen rules without explicit instruction
- do not assume a permissive config is acceptable beyond setup needs

---

## Error handling

Use centralized error handling.

Rules:
- use `@ControllerAdvice`
- differentiate 4xx vs 5xx correctly
- return a consistent error payload
- keep messages friendly and safe
- avoid exposing internal implementation details

Preferred error shape:

```json
{
  "code": "ERROR_CODE",
  "message": "Friendly message",
  "details": {}
}
```

Do not expose:
- stack traces
- SQL statements
- entity internals
- security details
- sensitive identifiers
- raw exception messages when they leak internal details

---

## Database and Flyway rules

Schema changes must be migration-based.

Mandatory:
- all schema changes go through Flyway
- create new migration files only
- never edit old migrations already applied in shared environments
- keep SQL scripts clear, explicit, and reviewable
- prefer deterministic migrations
- review indexes and constraints when use cases require them

Do not:
- use `ddl-auto=update`
- hide schema changes outside migration files
- make destructive changes without explicit validation and impact awareness
- introduce ambiguous migration names

Migration practices:
- use clear versioned migration names
- keep migrations focused
- ensure startup/migration behavior remains understandable
- validate Flyway when schema changes are introduced

---

## Performance and query discipline

Backend changes must consider operational performance.

Rules:
- avoid N+1 queries
- paginate large list endpoints
- fetch only what is needed
- avoid uncontrolled serialization depth
- review filter-heavy endpoints carefully
- be careful with dashboard, patient, appointments, and history-heavy flows
- evaluate query complexity when adding joins, aggregates, or computed filters

If a query becomes critical:
- review indexing
- review projection strategy
- review payload size
- review transaction boundaries

Do not optimize blindly, but do not ignore obvious hot paths either.

---

## Observability and logging

Logs must be useful without leaking sensitive data.

Rules:
- prefer structured logs in JSON
- include `traceId` when available
- log operationally relevant events
- keep logs concise and intentional
- avoid ornamental noise

Never log:
- DNI
- diagnoses
- clinical notes
- prescriptions
- patient-identifiable medical text
- tokens
- passwords
- secrets
- raw credentials

Do not log full payloads if they may contain PII or clinical content.

Recommended structured fields:
- timestamp
- level
- service
- traceId
- message

Optional extra fields must be sanitized.

---

## Testing rules

Relevant backend changes should include appropriate verification.

When applicable, cover:
- service unit tests
- integration tests
- authorization tests
- validation tests
- repository tests for complex queries
- mapper tests when mappings are non-trivial

Minimum expectation by type of change:
- business rule change -> service tests
- endpoint/contract change -> controller/integration verification
- security change -> auth/authorization verification
- query change -> repository verification when complexity justifies it
- migration change -> Flyway validation and startup sanity check

If tests do not exist:
- do not pretend full confidence
- provide manual verification steps
- mention residual risk explicitly

---

## Backend verification commands

Run from:

`akine-api/`

Use the commands that match the scope of the change:

```bash
mvn test
mvn test -Dtest=ClassName
mvn clean package -DskipTests
mvn spring-boot:run
mvn jacoco:report
mvn flyway:validate
```

Verification must be honest:
- do not claim tests passed if they were not run
- do not claim Flyway is valid if schema changed and validation was skipped
- do not claim the API contract is safe if it changed and was not reviewed

---

## Backend definition of done

A backend task is done only if all applicable conditions are met:

- requested behavior is implemented
- architecture boundaries were respected
- security and tenant isolation were preserved
- relevant tests pass
- build passes
- Flyway validates if schema changed
- contract changes are reflected in DTOs, docs, and tests
- no secrets or sensitive data leaked
- risks and edge cases are explicitly stated

Do not present backend work as complete if validation is partial.

---

## Forbidden without explicit request

Do not do any of the following unless explicitly requested:
- put business logic in controllers
- expose entities in endpoints
- add large dependencies without strong justification
- change security base by intuition
- rewrite old applied migrations
- refactor unrelated modules
- silently change API contracts
- weaken validation rules for convenience
- bypass consultorio isolation
- hide breaking changes behind partial compatibility tricks

---

## Required response format for backend tasks

When reporting a backend change, always state:

- goal understood
- files/packages affected
- endpoint/service/repository impact
- contract impact
- security/tenant impact
- database impact
- commands used for verification
- remaining risks, assumptions, or edge cases

Be explicit.  
Do not present backend work as complete if validation is partial.

---

## Final operating rule

When in doubt, Gemini must:
- protect data
- preserve architecture
- reduce scope
- keep contracts explicit
- prefer safe progress over speculative rewrites