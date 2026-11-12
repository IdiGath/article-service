# 01 Introduction and Goals

## 1.1 Requirements Overview

article-service is a backend REST service for managing blog articles. Articles
are stored in PostgreSQL as Markdown and exposed via a REST API (get, create,
update and delete by slug — the URL-friendly identifier generated from the
title; search with pagination by text and tags). It is
the first finished service of a personal blog platform.

## 1.2 Quality Goals

| Priority | Quality goal | Concrete target |
|---|---|---|
| 1 | Developer experience | `make dev` starts the service with hot reload; DevServices auto-provisions PostgreSQL, no manual DB setup |
| 2 | Testability | Unit + endpoint tests via `make test`; coverage report via `./mvnw clean verify` (JaCoCo) |
| 3 | Operability | 30s HTTP read/idle timeouts configured; JDBC statement timeout 5s in prod (`statement_timeout` in the %prod JDBC URL); schema validated at startup (`database.generation=validate`) |
| 4 | API transparency | OpenAPI spec + Swagger UI always available (`/q/openapi`, `/q/swagger-ui`) |

## 1.3 Stakeholders

- **Idigath** — owner, developer, and reviewer of all changes (human-directed development; no change is merged without his review)
- Consumers of the REST API (planned: blog-gateway and other services of the blog platform)
