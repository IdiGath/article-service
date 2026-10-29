# article-service

This project provides a backend service for managing blog articles.

**Status:** Work in progress. GET article by title, search with pagination, and DELETE by title are implemented and tested. Create (POST) and update (PUT) are stubs returning hardcoded responses. Not production-ready.

## Architecture
The project consists of:
- **PostgreSQL Database** (Liquibase migrations in `src/main/resources/db/` and `./docker-compose.yml`)
- **Quarkus service** for data processing and REST API

## Key Features
- **Article storage** in PostgreSQL (Markdown format)
- **Article search** by title and tags (case-insensitive, supports multiple tags with AND logic)
- **Pagination** for article lists
- **Article deletion** by exact title match (case-insensitive)
- **REST API** for content access
- TODO **Create and update operations** (create, update articles)
- TODO **AI-powered article classification and spell-checking** (on article creation via POST: auto-suggest tags/categories from content, check spelling without altering writing style — see Vikunja task)
- TODO **Markdown-to-HTML parsing** (converts stored Markdown to HTML before returning it to the client)
- TODO **Container image build** (wire up the generated Dockerfiles in `src/main/docker/` with make targets and documentation)

## Workflow
1. Articles are stored in the database as **Markdown**.
2. Articles are returned to the consumer via REST endpoints.

## Development Process and AI Assistance

This project is developed with the assistance of [Mistral Vibe](https://mistral.ai), a CLI coding agent by Mistral AI. All work is human-directed and human-reviewed: design decisions, review, corrections, and final approval of every change are made by the maintainer. Code is never merged without human oversight.

## Getting Started

For development, this project uses **Quarkus DevServices** which automatically manages a PostgreSQL container — no manual Docker setup needed. For running PostgreSQL standalone (e.g., production mode), use `docker-compose.yml`:

```bash
docker compose up -d
```

### Prerequisites
- Java 21+
- Maven (or use the provided `./mvnw` wrapper)
- Docker or Podman (for DevServices to auto-start PostgreSQL)
- `gettext` (provides `envsubst`, required for `make generate-dev-sql`)

### Using the Makefile

| Command | Description |
|---------|-------------|
| `make dev` | Start Quarkus in development mode (DevServices auto-starts PostgreSQL) |
| `make build` | Build the project with Maven |
| `make test` | Run unit and integration tests |
| `make generate-dev-sql` | Generate the DevServices init SQL script |
| `make clean-dev-sql` | Clean up generated SQL files |
| `make reset-db` | Stop containers, delete database data and generated SQL (full reset) |

### Quick Start
```bash
# Generate the database init script (required for DevServices)
make generate-dev-sql

# Start development server with hot reload
make dev
```

The application will be available at `http://localhost:8080`. DevServices automatically starts PostgreSQL and applies the database schema via Liquibase.

## Configuration

The service uses environment variables for database credentials. Copy `.env.example` to `.env` in the project root and fill in real values (`.env` is listed in `.gitignore` and must not be committed).

**Dev profile** (used by `make dev`, `make generate-dev-sql`, and `docker-compose.yml`):

| Variable | Description |
|---|---|
| `POSTGRES_PWD` | Password for the PostgreSQL superuser in the Docker container |
| `DEV_ARTICLE_SERVICE_USER_PWD` | Password for the application database user |
| `DEV_LIQUIBASE_USER_PWD` | Password for the Liquibase migration user |

**Prod profile:**

| Variable | Description |
|---|---|
| `PROD_ARTICLE_SERVICE_USER_PWD` | Password for the application database user |
| `PROD_LIQUIBASE_USER_PASSWORD` | Password for the Liquibase migration user |

Example `.env` for development:

```bash
POSTGRES_PWD=postgrespassword
DEV_ARTICLE_SERVICE_USER_PWD=devpassword
DEV_LIQUIBASE_USER_PWD=liquibasepassword
```

## OpenAPI
- [Swagger UI](http://localhost:8080/q/swagger-ui/)
- [OpenAPI Spec](http://localhost:8080/q/openapi)
- [OpenAPI Spec (JSON)](http://localhost:8080/q/openapi?format=json)

## Sources that were useful
- [Simplified Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Validation with Hibernate Validator](https://quarkus.io/guides/validation)
- [Configure data sources in Quarkus](https://quarkus.io/guides/datasource)
- [Writing REST Services with Quarkus REST (formerly RESTEasy Reactive)](https://quarkus.io/guides/rest)
- [Mapstruct](https://mapstruct.org/)
- [Jackson serialization support for Quarkus REST](https://quarkus.io/guides/rest#json-serialisation)
- [Using OpenAPI and Swagger UI in Quarkus](https://quarkus.io/guides/openapi-swaggerui)
- [Testing Your Application in Quarkus](https://quarkus.io/guides/getting-started-testing)
- [Continuous Testing in Quarkus](https://quarkus.io/guides/continuous-testing)
- [Using liquibase in Quarkus](https://quarkus.io/guides/liquibase)
- [Development and Testing of Quarkus applications using Testcontainers](https://testcontainers.com/guides/development-and-testing-quarkus-application-using-testcontainers)
- [Context and Dependency Injection](https://quarkus.io/guides/cdi)

## Testing

Run tests using the Makefile or Maven directly:

```bash
make test
# or
./mvnw test
```

This runs all tests via Maven Surefire plugin. Currently there are no separate Maven Failsafe integration tests (no `*IT.java` files or `src/it/` directory). The `<skipITs>true</skipITs>` property in `pom.xml` ensures that if Failsafe integration tests are added in the future, they will be skipped by default.

To run tests with coverage reports:

```bash
./mvnw clean verify
```

Coverage reports are generated at `target/site/jacoco/index.html`.
