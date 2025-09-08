### Setting-up environment
- `make docker-up` to run containers
- Wait atleast 2 minutes to finish db migrations.

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
- Clean Architecture combined with Ports & Adapters


### Getting Started

#### Folder Structure
- **kratos** - contains kratos configuration
- **misc** - miscellaneous files like schemas, templates
- **domain** - contains domain components
  - **port** - port interfaces/records that being injected in Service and implemented in Infra
  - **service** - service layer
- **config** - contains configurations beans 
- **errors** - contains custom errors and global exception
- **infra** - contains infra services like psql, kratos, redis, and minio 
- **interfaces** - controller layer
  - **web** - REST Endpoints
- **utils** - contains helpers and utilities
- **resources** - contains resources
  - **db/changelog** - Liquibase changelog and Initial SQL Structure
  - **db.config** - Liquibase Properties and initial databases

### Configuration

### API Endpoints

### Contributing

### License
