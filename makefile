# Default Environment variable. For prod just use f.e. "prod make generate-init-sql"
ENVIRONMENT ?= dev
DEV_TEMPLATE := ./src/main/resources/db/dev/templates/init-dev-user-template.sql
DEV_OUTPUT := ./src/main/resources/db/dev/target/init-dev-user.sql
TEST_TEMPLATE := ./src/test/resources/db/templates/init-test-user-template.sql
TEST_OUTPUT :=  ./src/test/resources/db/target/init-test-user.sql

.PHONY: init generate-init-sql build test up down full run
init:
	@if [ -z "$(CONTAINER_ENGINE)" ]; then \
		echo "🐳 Which container engine should be used?"; \
		echo "1) Podman"; \
		echo "2) Docker"; \
		read -p "Choice (1/2): " choice; \
		case $$choice in \
			1) export CONTAINER_ENGINE=podman;; \
			2) export CONTAINER_ENGINE=docker;; \
			*) echo "❌ Wrong choice"; exit 1;; \
		esac; \
		export CONTAINER_ENGINE; \
	fi

generate-init-sql:
	@if [ "$(ENVIRONMENT)" = "prod" ]; then \
		mkdir -p ./db/target; \
		envsubst < ./db/init.sql.template > ./db/target/init.sql; \
	else \
		set -a && source ./.env && set +a && \
		mkdir -p ./db/target; \
		envsubst < ./db/init.sql.template > ./db/target/init.sql; \
	fi

generate-dev-test-sql:
	@set -a; \
	. ./.env; \
	set +a; \
	mkdir -p $(dir $(DEV_OUTPUT)); \
	envsubst < $(DEV_TEMPLATE) > $(DEV_OUTPUT); \
	mkdir -p $(dir $(TEST_OUTPUT)); \
	envsubst < $(TEST_TEMPLATE) > $(TEST_OUTPUT)

clean-dev-test-sql:
	@rm -f $(DEV_OUTPUT) $(TEST_OUTPUT) 2>/dev/null || true
	@echo "🧹 Cleaned up generated SQL files"

up:
	@[ "$(CONTAINER_ENGINE)" = "podman" ] && { \
		podman-compose up -d postgres; \
	} || { \
		docker compose up -d postgres; \
	}

build:
	mvn article-service clean package

down:
	@[ "$(CONTAINER_ENGINE)" = "podman" ] && podman-compose down || docker compose down

full: init generate-init-sql up build
