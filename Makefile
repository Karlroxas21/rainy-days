DOCKER_COMPOSE = docker-compose
PROFILE = dev

## Add start
start:
	./mvnw spring-boot:run

## Start Docker
docker-up:
	$(DOCKER_COMPOSE) --profile $(PROFILE) up -d

## Stop Docker
docker-down:
	$(DOCKER_COMPOSE) --profile $(PROFILE) down
    # If Windows
	rmdir /s /q .data
	# Linux/Mac
	## rm -rf .data

## Drop all db
make drop-db:
	liquibase dropAll \
	--url="jdbc:postgresql://localhost:5432/rainydays" \
    --username=dev \
    --password=dev \

## Liquibase Migratehistory
migrate-history:
	liquibase  \
	--url="jdbc:postgresql://localhost:5432/rainydays" \
	--username=dev \
	--password=dev \
	history