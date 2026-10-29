# Generate SQL for DevServices init script
DEV_TEMPLATE := ./src/main/resources/db/dev/templates/init-dev-user-template.sql
DEV_OUTPUT := ./src/main/resources/db/dev/target/init-dev-user.sql

.PHONY: generate-dev-sql clean-dev-sql reset-db build dev test

generate-dev-sql:
	@set -a; \
	. ./.env; \
	set +a; \
	mkdir -p $(dir $(DEV_OUTPUT)); \
	envsubst < $(DEV_TEMPLATE) > $(DEV_OUTPUT);

clean-dev-sql:
	@rm -f $(DEV_OUTPUT) 2>/dev/null || true
	@echo "Cleaned up generated SQL files"

reset-db:
	@docker compose down -v 2>/dev/null || true
	@rm -rf ./postgres_data/* 2>/dev/null || true
	@$(MAKE) clean-dev-sql
	@echo "Database reset: containers stopped, data and generated SQL removed"

build:
	./mvnw clean package

dev:
	./mvnw quarkus:dev

test:
	./mvnw test
