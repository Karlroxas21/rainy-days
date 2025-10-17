# RainyDays Backend Service
An emergency Fund manual tracker for you and your loved ones - building your safety net.

<!-- 
[![Build Status](https://github.com/my-user/my-app/workflows/CI/badge.svg)](https://github.com/my-user/my-app/actions) -->
[![Code Coverage](https://codecov.io/gh/my-user/my-app/branch/main/graph/badge.svg)](https://codecov.io/gh/my-user/my-app)
[![License](https://img.shields.io/badge/License-CC%20BY--NC--ND%204.0-lightgrey.svg)](LICENSE)

## Table of Contents
* [Introduction](#introduction)
* [Features](#features)
* [Prerequisites](#prerequisites)
* [Getting Started](#getting-started)
* [Configuration](#configuration)
* [API Endpoints](#api-endpoints)
* [Deployment](#deployment)
* [Contributing](#contributing)
* [License](#license)

### Introduction
We Filipino's tend to have not save for "Rainy Days" or we can say Emergencies. In this SaaS, we can help you achieve your *RainyDays* budget easily, you can track by groups and see your progress. The problem we are trying to solve here is to give people a platform to save for rainy days and visually see the progress.

Feel free to donate or buy us a coffee :)

Original idea was from Karl Marx Roxas (me) and I asked my former college classmate and a close friend to help me execute this project (Geli Robiso).

### Features
1. Personal Emergency Fund Tracker
2. Groups Emergency Fund Tracker
3. Set goals either personal or groups
4. Dashboard to see your metrics
5. Member rankings (available in group only)

### Pre-requisites
- Docker
- JDK 21.0.2
- IntelliJ IDEA/VSCode
- Postman
- Make

### Getting Started
- `make docker-up` to run containers
- Wait at least 2 minutes to finish db migrations.
- Start the server or enter `make start` in terminal

#### Folder Structure
- **application** - contains components services & ports
- **config** - contains different configurations
- **errors** - contains custom error handling and global errors
- **infra** - contains all different services running independently
- **interfaces.web** - contains all REST endpoints or controllers
- **utils** - contains all utilities/helpers
- **resources.db.changelog** - Liquibase changelog and Initial SQL Structure
- **resources.db.config** - Liquibase Properties and initial databases
- **test** - contains all unit test cases (infra, service, and controller)

### Configuration

### API Endpoints
- Run the server and go to `http://localhost:8080/swagger-ui/index.html#/` to see the OpenAPI definition or go to `docs` folder to open postman collection.

### Contributing
- Email to `karlm.roxa@gmail.com` or just make a PR following the template
