# Default Environment variable. For prod just use f.e. "prod make generate-init-sql"
ENVIRONMENT ?= dev

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
