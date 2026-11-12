# 06 Runtime View

## 6.1 Startup (dev profile)

1. `make dev` → `./mvnw quarkus:dev`
2. Quarkus DevServices starts a `postgres:18` container and applies the init script
3. Liquibase applies migrations from `db/changeLog.xml` (`migrate-at-start=true`)
4. Hibernate validates the schema (`database.generation=validate`) — on mismatch, boot fails
5. The application listens on `:8080`; Swagger UI is always available

## 6.2 GET `/api/articles/{slug}` — read an article

```mermaid
sequenceDiagram
    participant GW as blog-gateway
    participant EP as ArticleEndpoint
    participant SV as ArticleService
    participant DB as PostgreSQL
    GW->>EP: GET /api/articles/{slug}
    EP->>SV: findArticleBySlug(slug)
    SV->>DB: SELECT via Panache (Article)
    DB-->>SV: row
    SV->>SV: MapStruct map entity → ArticleDto
    SV-->>EP: ArticleDto
    EP-->>GW: 200 JSON
```

## 6.3 GET `/api/articles/search` — search with pagination

Gateway → endpoint → `searchArticleOverview(page, text, tags)` → DB query
(case-insensitive title/text match, AND logic across tags) → `ArticleOverview`
(list of previews + pagination meta) → 200 JSON.

## 6.4 DELETE `/api/articles/{slug}` — delete by exact slug

Gateway → endpoint → `deleteArticleBySlug(slug)` → transactional delete → 204,
or 404 when no article has this slug.

## 6.5 POST / PUT

POST: create article → unique slug generated from the title → 201 + `Location`
header + `ArticleDto` body. PUT: update by slug (slug never changes) → 200 +
`Last-Modified` header + `ArticleDto` body.
