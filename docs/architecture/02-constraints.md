# 02 Constraints

| Constraint | Description |
|---|---|
| Platform | Java 21 (`maven.compiler.release=21` in `pom.xml`) |
| Framework | Quarkus 3.32.4 (`quarkus.platform.version`); decision recorded in [§9 ADR-0001](09-architectural-decisions.md) |
| Database | PostgreSQL only (`quarkus.datasource.db-kind=postgresql`); dev via DevServices image `postgres:18`, standalone via `docker-compose.yml` |
| Schema management | Liquibase, changelog `db/changeLog.xml`, migrates at startup; Hibernate validates schema (`database.generation=validate`) |
| Build | Maven via the provided wrapper `./mvnw`; tasks exposed as `makefile` targets (`dev`, `build`, `test`, `generate-dev-sql`, `reset-db`) |
| Secrets | Credentials only through env vars / `.env`; `.env` is in `.gitignore`, template in `.env.example` — never committed |
| Timeouts | HTTP read/idle timeout 5s, JDBC timeout 5 (`application.properties`) |
| License | MIT License, copyright Idigath |
| Development process | AI-assisted development (Mistral Vibe) disclosed in the README; every change is human-directed and human-reviewed by Idigath |
| Docs language | English for all project documentation; architecture documented in `docs/architecture/` (arc42) |
