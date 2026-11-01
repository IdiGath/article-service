# ADR-0001: Use Quarkus as the application framework

- **Status:** accepted
- **Date:** 2026-11-01

## Context

article-service is a greenfield backend for managing blog articles: a REST API
in front of a PostgreSQL database, developed as one of several services under
the blog project. The service needs JAX-RS REST endpoints,
JPA/Hibernate persistence, Liquibase schema migrations, bean validation, and
generated OpenAPI documentation. A Java framework had to be chosen before the
first endpoint was written.

## Decision Drivers

- Fast dev loop: hot reload and automatic provisioning of the database in
  development mode without manual Docker setup.
- Low operational footprint for a small CRUD service (startup time, memory).
- First-class support for the required building blocks: REST, Hibernate ORM,
  JDBC PostgreSQL, Liquibase, Bean Validation, OpenAPI/Swagger UI.
- Build-time optimization and native-image capability for future container
  deployments.

## Considered Options

1. **Quarkus** — Red Hat's Kubernetes-native Java framework, build-time
   bytecode processing, extensions for REST/Hibernate/Liquibase, DevServices.
2. **Spring Boot** — the de-facto standard Java framework, large ecosystem,
   Spring Data / Spring MVC abstractions over the same libraries.

## Decision

We chose **Quarkus 3.32.4** (see `quarkus.platform.version` in `pom.xml`,
packaging `quarkus`) on **Java 21**. The deciding factor was the development
experience for this service's shape: DevServices starts a PostgreSQL
container automatically in dev mode (`make dev`) and Liquibase runs at
startup, so no manual database setup is needed, while build-time processing
keeps startup and memory low and leaves a native-image path open (the
Failsafe plugin is already configured with `native.image.path`).

## Consequences

### Positive

- `quarkus-rest-jackson` provides the REST layer with Jackson serialization;
  `quarkus-smallrye-openapi` serves Swagger UI and the OpenAPI spec at
  `/q/swagger-ui/` and `/q/openapi` with `quarkus.swagger-ui.always-include=true`.
- `quarkus-hibernate-orm-panache` + `quarkus-hibernate-orm` cover persistence
  with schema validation (`quarkus.hibernate-orm.database.generation=validate`).
- `quarkus-liquibase` applies migrations from `db/changeLog.xml` at startup
  (`quarkus.liquibase.migrate-at-start=true`).
- DevServices (`quarkus.datasource.devservices.*`, image `postgres:18`)
  replaces `docker-compose.yml` for local development; docker-compose remains
  for running PostgreSQL standalone in prod mode.
- Dev mode (`./mvnw quarkus:dev`, `make dev`) gives hot reload and continuous
  testing.

### Negative / Trade-offs

- Quarkus REST (RESTEasy Reactive) is not Spring MVC; code and muscle memory
  from Spring Boot do not transfer 1:1.
- Panache repositories/entities differ from Spring Data JPA; team members
  coming from Spring need to learn the Panache idioms.
- Smaller ecosystem than Spring Boot for edge concerns outside the used
  extensions.

### Neutral / Follow-ups

- MapStruct 1.6.3 is used for DTO/entity mapping, independent of the framework
  choice.
- Tests use `quarkus-junit`, `quarkus-junit-mockito`, REST Assured, AssertJ;
  coverage via JaCoCo (`make test`, `./mvnw clean verify`).
