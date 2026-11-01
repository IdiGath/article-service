# 03 Context and Scope

## 3.1 Business Context

article-service is the first finished service of a personal blog platform.
External actors:

- **Blog gateway** — the only direct consumer of the REST API (planned; not yet
  implemented). It calls article-service to read, search, create, update, and
  delete articles.
- **Frontend** — does **not** call article-service directly; it talks only to
  the gateway, which acts as intermediary.
- **Idigath (author)** — maintains articles through the API (via the gateway).
- **PostgreSQL** — external system: the service reads/writes articles, sources,
  and tags there; schema managed by Liquibase.

## 3.2 Technical Context

- REST/JSON over HTTP (Quarkus REST + Jackson), base path `/api/articles` (`ArticleEndpoint`)
- JDBC to PostgreSQL (`quarkus-jdbc-postgresql`), credentials from env vars per profile (`%dev` / `%prod`)
- OpenAPI/Swagger UI exposed at `/q/openapi` and `/q/swagger-ui`
- In dev, PostgreSQL runs as a DevServices container (`postgres:18`); in prod,
  as a standalone container via `docker-compose.yml`
