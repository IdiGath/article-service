# 07 Deployment View

> Note: the prod part of this section is a draft — it will be revised when
> production deployment is tackled.

## 7.1 Dev environment (developer machine)

- **Node: dev machine**
  - article-service in JVM mode: `make dev` (`./mvnw quarkus:dev`), HTTP on `:8080`
  - DevServices container `postgres:18` on port `8085`, init script generated via `make generate-dev-sql`
  - Connection: JDBC, credentials from `.env` (`DEV_ARTICLE_SERVICE_USER_PASSWORD`, `DEV_LIQUIBASE_USER_PASSWORD`)

## 7.2 Prod / standalone environment (host with Docker)

- **Node: Docker host**
  - Container `article-postgres` (`postgres:18`, port `8085:5432`, volume `./postgres_data`, network `blog-network`, password from `POSTGRES_PASSWORD`) — `docker-compose.yml`
  - article-service deployed as a build artifact: runner jar (`target/quarkus-app/`) or native binary; Dockerfiles exist in `src/main/docker/` (`Dockerfile.jvm`, `Dockerfile.legacy-jar`, `Dockerfile.native`, `Dockerfile.native-micro`) but image build is TODO and not wired into make targets
  - Config: `%prod` profile, JDBC URL `jdbc:postgresql://localhost:8085/article`, secrets from env (`PROD_ARTICLE_SERVICE_USER_PASSWORD`, `PROD_LIQUIBASE_USER_PASSWORD`)
  - **Prod executes only `db/migration/`**: the `dev-data` seed changeset has Liquibase `context="dev"` and is skipped in prod (`%prod.quarkus.liquibase.contexts=prod`); it runs only in dev (`%dev.quarkus.liquibase.contexts=dev`)

## 7.3 Mapping artifacts → nodes

| Artifact | Deployed on | Notes |
|---|---|---|
| `quarkus-app` runner jar / native runner | Docker host (prod) | image build TODO |
| `postgres:18` image | Docker host, port 8085; DevServices on dev machine | |
| `.env` (dev) / env vars (prod) | respective node | never committed |

The future gateway communicates with the service over HTTP — a separate node
outside this service's scope.
