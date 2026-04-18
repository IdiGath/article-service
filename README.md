# article-service

This project provides a backend service for managing blog articles.

## Architecture
The project consists of:
- **PostgreSQL Database** (configuration in `./db` and `./docker-compose.yml`)
- **Quarkus service** for data processing and REST API

## Key Features
- **Article storage** in PostgreSQL (Markdown format)
- **CRUD operations** for articles
- **REST API** for content access
- **Markdown-to-HTML parsing** (converts stored Markdown to HTML before returning it to the client)

## Workflow
1. Articles are stored in the database as **Markdown**.
2. The service parses Markdown into HTML on request.
3. HTML is returned to the consumer via REST endpoints.

## Getting Started

//TODO

## Sources that were useful
- [Simplified Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Validation with Hibernate Validator](https://quarkus.io/guides/validation)
- [Configure data sources in Quarkus](https://quarkus.io/guides/datasource)
- [Writing REST Services with Quarkus REST (formerly RESTEasy Reactive)](https://quarkus.io/guides/rest)
- [Mapstruct](https://mapstruct.org/)
- [Jackson serialization support for Quarkus REST](https://quarkus.io/guides/rest#json-serialisation)
- [Connect to the PostgreSQL database via JDBC](https://quarkus.io/guides/datasource)

## Testing

Coverage reports are generated at `target/site/jacoco/index.html` after running:

```bash
./mvnw clean verify
```
