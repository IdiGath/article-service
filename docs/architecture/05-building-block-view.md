# 05 Building Block View

## 5.1 Level 1 — whitebox `article-service` (package `de.idigath`)

```
article-service
├── specification   ArticleServiceApp          — application entry point (@QuarkusMain)
├── web             ArticleEndpoint            — REST interface, @Path("/api/articles")
├── service         ArticleService             — business logic
├── entity          Article, Source, Tag      — Panache entities (JPA)
├── model           ArticleDto, ArticlePreviewDto, ArticleOverview, SourceDto, TagDto
├── mapper          ArticleMapper, ArticlePreviewMapper, SourceMapper, TagMapper (MapStruct)
└── exception       QueryTimeoutExceptionMapper, ConstraintViolationExceptionMapper
```

External systems: **PostgreSQL** (JDBC via Hibernate), schema via Liquibase (`db/changeLog.xml`).

## 5.2 Level 2 — `web.ArticleEndpoint`

| Method | Path | Operation | Status |
|---|---|---|---|
| GET | `/api/articles/{title}` | article by title → `ArticleDto` | implemented, tested |
| GET | `/api/articles/search?page=&text=&tags=` | search + pagination → `ArticleOverview` | implemented, tested |
| POST | `/api/articles` | create article | stub |
| PUT | `/api/articles/{title}` | update article (case-insensitive exact title) | implemented, tested |
| DELETE | `/api/articles/{title}` | delete by exact title (case-insensitive) | implemented, tested |

## 5.3 Interfaces

In arc42, an *interface* is a point of interaction between building blocks or
with external systems (not a Java `interface`).

- **I1**: REST/JSON API (`/api/articles`, OpenAPI at `/q/openapi`) — consumer: blog-gateway
- **I2**: JDBC → PostgreSQL (Hibernate ORM)
- **I3**: Liquibase changelog `db/changeLog.xml` → database schema
