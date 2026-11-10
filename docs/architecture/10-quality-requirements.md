# 10 Quality Requirements

## 10.1 Quality Tree

| Quality | Motivation | Concrete criterion |
|---|---|---|
| Developer experience | Fast inner loop for a solo-maintained service | `make dev` → hot reload, DB auto-provisioned; no manual setup |
| Testability | Changes reviewed safely | `make test` green; JaCoCo report at `target/site/jacoco/index.html` (numeric coverage target: not set yet) |
| Operability | Fail fast on misconfiguration | Schema validated at boot (`database.generation=validate`); HTTP read/idle timeouts = 30s; JDBC statement timeout = 5s in prod only (`statement_timeout=5000` in the %prod JDBC URL) |
| API transparency | Gateway integration without guesswork | OpenAPI at `/q/openapi`, Swagger UI always on |
| Security | M2M calls only from trusted services | OAuth2 + mTLS per ADR-0002 — pending, currently none |

## 10.2 Quality Scenarios

| # | Stimulus | Response | Measure |
|---|---|---|---|
| QS1 | DB schema drift vs entities | Boot fails with validation error | Startup aborts, no partial state |
| QS2 | Slow downstream DB query | PostgreSQL statement timeout fires | ≤ 5s in prod (`statement_timeout=5000` in the %prod JDBC URL); not set in dev/test |
| QS3 | Gateway calls API without a valid token | 401 (once ADR-0002 is implemented) | Not measurable yet — security pending |
| QS4 | Dev restarts the service | DevServices reuses/starts PostgreSQL, Liquibase re-runs idempotently | Dev loop < 1 min (informal) |
