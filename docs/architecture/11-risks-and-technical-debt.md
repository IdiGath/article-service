# 11 Risks and Technical Debt

## 11.1 Risks

| ID | Risk | Impact | Mitigation |
|---|---|---|---|
| R1 | No authentication on the API (ADR-0002 pending) | Anyone reaching the port can read/delete articles | Network isolation (gateway-only assumption) until ADR-0002 lands |
| R2 | Prod deployment undefined — Dockerfiles exist but image build is not wired to make targets | Prod rollout blocked | §7 stays a draft; wire images when prod is tackled |
| R3 | identity-provider becomes a hard runtime dependency (token issuance + JWKS) | article-service cannot validate tokens if IdP is down | Accepted for now; consider cached JWKS |
| R4 | Slug is the public identifier (get/put/delete by slug); title stays unique case-insensitively | Slug collisions on create (e.g. "My Title" vs "my-title") | Unique index `uq_articles_slug` + numeric suffix on collision; title CI index `uq_articles_title_ci` |
| R5 | Single maintainer | Bus factor | Human review process documented; docs in English |

## 11.2 Technical Debt

Feature-level TODOs (container image build,
Markdown→HTML rendering, AI classification, coverage target) are tracked in a
single place: [README → Key Features](../README.md#key-features). This section
deliberately does not duplicate them.
