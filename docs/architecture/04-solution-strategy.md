# 04 Solution Strategy

| # | Strategy | Motivation |
|---|---|---|
| 1 | **Quarkus 3.32.4 on Java 21** as the application framework | Fast startup, low memory, build-time optimization; native-image path stays open (decision in §9) |
| 2 | **Layered architecture**: HTTP interface isolated in the `web` layer → `service` (business logic) → `entity` (Panache), with DTOs and MapStruct mappers between layers | Clear separation of concerns, testability of each layer |
| 3 | **PostgreSQL + Hibernate ORM (Panache)**, schema via **Liquibase** at startup, `validate` at runtime | Reliable persistence with versioned, reviewable migrations; schema drift caught at boot |
| 4 | **REST architectural style** exposed as a **JSON API** (JAX-RS endpoint implementation, Jackson serialization) with generated **OpenAPI/Swagger UI** | Contract transparency for the gateway integration |
| 5 | **DTO mapping with MapStruct** (compile-time generated) | No runtime reflection cost, type-safe mapping entity ↔ DTO |
| 6 | **Profile-based configuration** (`%dev` / `%prod`) with secrets only in env vars / `.env` | Same artifact for dev and prod; no credentials in git |
| 7 | **DevServices for dev, docker-compose for standalone DB** | Zero-setup dev loop (`make dev`), reproducible environments |
| 8 | **Tests**: JUnit 5 + Mockito + REST Assured + AssertJ, coverage via JaCoCo | Quality goal 2 (testability); `make test`, `./mvnw clean verify` |
