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

## Testing

Coverage reports are generated at `target/site/jacoco/index.html` after running:

```bash
./mvnw clean verify
```

# article-service

This project manages blog articles.
It consists of PostgreSQL Database, which specialization can be found in ./db and ./docker-compose.yml,
and quarkus service to work with it.

The main goals of this projects are:
- data articles storage
- CRUD-access to the data
- REST-access to the contect

All articles are stored in markdown form in the database.
The service will parse the content and return in via REST as HTML to the consumer.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/article-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)


[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
