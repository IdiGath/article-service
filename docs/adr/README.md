# Architecture Decision Records (ADR)

This directory records significant architectural decisions for article-service
using the ADR format (see [template.md](template.md)).

Rules:

- One decision per file, named `NNNN-<short-title>.md` with a sequential
  number.
- Records are immutable: to change a decision, add a new ADR with status
  `accepted` and mark the old one `superseded by ADR-NNNN`.
- Statuses: `proposed` → `accepted` / `rejected` / `deprecated` /
  `superseded by ...`.

## Index

| ADR | Title | Status |
|-----|-------|--------|
| [0001](0001-use-quarkus-as-application-framework.md) | Use Quarkus as the application framework | accepted |
