CORE_SERVICES = \
	jwizard-rabbitmq \
	jwizard-redis \
	jwizard-postgres \
	jwizard-neo4j

UI_SERVICES = \
	jwizard-redis-ui \
	jwizard-postgres-ui

.PHONY: up-core up-ui up-all down clean rec rec-all hard hard-all

# up all core services (without ui panels)
up-core:
	docker compose up -d $(CORE_SERVICES)

# up all ui panels
up-ui:
	docker compose up -d $(UI_SERVICES)

# up all (ui panels + core services)
up-all:
	docker compose up -d

# down all without removing data
down:
	docker compose down

# down all and remove data
clean:
	docker compose down -v

# recreate container (soft reset)
# usage: make rec s=<service name>
rec:
	@test $(s)
	docker compose up -d --force-recreate --no-deps $(s)

# recreate all containers
rec-all:
	docker compose up -d --force-recreate

# down and up container (hard reset)
# usage: make hard s=<service name>
hard:
	@test $(s)
	docker compose stop $(s)
	docker compose rm -f $(s)
	docker compose up -d --no-deps $(s)

# down and up all containers
hard-all: down up-all

include Makefile.jws
include Makefile.tools
