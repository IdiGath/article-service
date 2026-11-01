# 12 Glossary

| Term | Definition |
|---|---|
| ADR | Architecture Decision Record — immutable record of a significant architectural decision |
| arc42 | The 12-section template this document follows |
| Authorization server | Component issuing OAuth2 tokens (here: identity-provider, `/oauth2/token`) |
| DevServices | Quarkus feature auto-provisioning dependencies in dev (here: `postgres:18` container) |
| DTO | Data Transfer Object — API payload in `model`, mapped from entities via MapStruct |
| Gateway (blog-gateway) | Planned sole direct consumer of the article-service API; the frontend talks only to the gateway |
| Identity-provider | Separate service (Spring Authorization Server) issuing tokens and exposing JWKS |
| JWKS | JSON Web Key Set (`/oauth2/jwks`) — public keys to validate JWT signatures |
| JWT | JSON Web Token — signed bearer token |
| Liquibase | Schema migration tool; `db/changeLog.xml` applied at startup |
| M2M | Machine-to-machine: service-to-service calls without a human user |
| mTLS | Mutual TLS — both sides authenticate with certificates; as OAuth2 client auth per RFC 8705 |
| OAuth2 client_credentials | Grant type for M2M: the client authenticates and receives an access token |
| Panache | Quarkus Hibernate ORM extension used for entities in `entity` |
| Resource server | Service accepting and validating access tokens (article-service, once ADR-0002 is implemented) |
