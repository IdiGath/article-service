# 08 Crosscutting Concepts

- **API concept**: REST/JSON contract implemented with JAX-RS annotations in `web`, DTOs in `model`, OpenAPI/Swagger UI generated from code (`/q/openapi`, `/q/swagger-ui`)
- **Persistence concept**: Panache entities in `entity`; schema versioned by Liquibase (`db/changeLog.xml`) and validated at startup; all DB access goes through `service`
- **Mapping concept**: entity ↔ DTO only via MapStruct mappers in `mapper` (compile-time generated)
- **Configuration concept**: `application.properties` + `%dev`/`%prod` profiles; secrets exclusively via env vars / `.env` (gitignored, template in `.env.example`)
- **Testing concept**: JUnit 5 + Mockito (unit), REST Assured (endpoint), AssertJ assertions; coverage via JaCoCo (`make test`, `./mvnw clean verify`)
- **Documentation concept**: all docs in English; architecture in `docs/architecture/` (arc42), decisions in §9; AI-assisted development disclosed in the README
- **Security concept**: machine-to-machine OAuth2 via the separate **identity-provider** service. The gateway authenticates to identity-provider (`/oauth2/token`, `client_credentials` grant) and receives a signed JWT bearer token with scopes; client authentication method is **mTLS** (RFC 8705) per ADR-0002. article-service acts as a **resource server**: it validates token signatures against identity-provider's `/oauth2/jwks` endpoint and enforces scopes. **Not yet implemented** — the API currently has no authentication.
