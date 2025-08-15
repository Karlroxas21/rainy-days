DOCKER_COMPOSE = docker-compose
PROFILE = dev

## Start Docker
docker-up:
	$(DOCKER_COMPOSE) --profile $(PROFILE) up -d

## Stop Docker
docker-down:
	$(DOCKER_COMPOSE) --profile $(PROFILE) down
