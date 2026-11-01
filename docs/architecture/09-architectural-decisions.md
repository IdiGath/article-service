# 09 Architectural Decisions

Architecture Decision Records (ADRs) for article-service. Records are
immutable: supersede a decision with a new ADR instead of editing an old one.

## ADR-0001 — Use Quarkus 3.32.4 on Java 21 as the application framework

- **Status:** accepted
- **Date:** 2026-11-01

**Context.** Greenfield REST service in front of PostgreSQL. The project was
initialized as a Spring Boot skeleton (commit `072e4b8`, 2025-09-26) and the
skeleton was replaced with a Quarkus one (commit `9c3f051`, 2026-03-24).

**Decision.** Quarkus 3.32.4 (`quarkus.platform.version`, packaging `quarkus`)
on Java 21. Rationale: DevServices auto-provisions `postgres:18` in dev,
Liquibase runs at startup, build-time processing keeps startup/memory low and
leaves a native-image path open (Failsafe is configured with
`native.image.path`).

**Consequences.**
- Positive: Quarkus REST + Jackson for the API, Panache for persistence,
  schema validation (`database.generation=validate`), OpenAPI/Swagger UI built
  in, hot reload via `make dev`.
- Trade-offs: Quarkus REST (RESTEasy Reactive) and Panache idioms differ from
  Spring MVC / Spring Data; smaller ecosystem than Spring Boot for edge
  concerns.

## ADR-0002 — M2M security via OAuth2 client_credentials with mTLS client authentication through identity-provider

- **Status:** accepted (implementation pending)
- **Date:** 2026-11-01

**Decision.** Service-to-service calls are secured with OAuth2
`client_credentials`. The client (blog-gateway) authenticates to
identity-provider's `/oauth2/token` via **mTLS** (RFC 8705) — certificate-based
client authentication instead of `client_secret_basic`. identity-provider
issues signed JWT bearer tokens with scopes; article-service acts as a
**resource server** and validates tokens via `/oauth2/jwks`.

**Current state.** article-service has **no authentication at all**.
identity-provider currently implements `client_secret_basic` and will be
extended to mTLS when security is implemented.

**Consequences.**
- Each service needs certificate/keystore management.
- identity-provider becomes a hard runtime dependency for token issuance and JWKS.
- mTLS remains a client-authentication method *within* OAuth2, not a separate
  protocol; the grant stays `client_credentials` and tokens stay JWT bearer.
