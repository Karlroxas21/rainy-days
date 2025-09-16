DOCKER_COMPOSE = docker-compose
PROFILE = dev

## Add start
start:
# 	./mvnw spring-boot:run
# 	Install Maven 3.9.11 Locally first to run this
	mvn spring-boot:run

test:
	mvn clean test

## Start Docker
docker-up:
	$(DOCKER_COMPOSE) --profile $(PROFILE) up -d

## Stop Docker
docker-down:
	$(DOCKER_COMPOSE) --profile $(PROFILE) down
    # If Windows
# 	rmdir /s /q .data
# 	Linux/Mac
	#rm -rf .data

clean:
#   If Windows
	rmdir /s /q .data
# 	Linux/Mac
# 	rimraf -rf .data

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

## Reload Maven
mvn-clean-resolve:
	mvn clean dependency:resolve