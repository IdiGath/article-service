# 11 Risks and Technical Debt

## 11.1 Risks

| ID | Risk | Impact | Mitigation |
|---|---|---|---|
| R1 | No authentication on the API (ADR-0002 pending) | Anyone reaching the port can read/delete articles | Network isolation (gateway-only assumption) until ADR-0002 lands |
| R2 | Prod deployment undefined — Dockerfiles exist but image build is not wired to make targets | Prod rollout blocked | §7 stays a draft; wire images when prod is tackled |
| R3 | identity-provider becomes a hard runtime dependency (token issuance + JWKS) | article-service cannot validate tokens if IdP is down | Accepted for now; consider cached JWKS |
| R4 | Title is the public identifier (get/delete by title, case-insensitive) | Two articles differing only by letter case collide | Unique constraint / normalization — to verify in schema |
| R5 | Single maintainer | Bus factor | Human review process documented; docs in English |

## 11.2 Technical Debt

Feature-level TODOs (create/update stubs, container image build,
Markdown→HTML rendering, AI classification, coverage target) are tracked in a
single place: [README → Key Features](../README.md#key-features). This section
deliberately does not duplicate them.
