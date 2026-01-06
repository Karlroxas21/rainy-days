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
	./mvnw liquibase:dropAll

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

## Build Spring Boot
package-skip-test:
	 mvn clean package -DskipTests

## Run Build files
run-jar:
	java -jar target/rainydays-0.0.1-SNAPSHOT.jar

## Build docker file
docker-build:
	docker build -t rainydays-service .

## Run Docker Image
docker-run:
	docker run -p 8080:8080 rainydays-service