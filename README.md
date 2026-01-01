# RainyDays Backend Service
This product is a family emergency fund coordination system designed for countries with weak healthcare safety nets.
It helps families pre-commit money, define clear emergency rules, and make fast, fair decisions when hospital bills hit without holding
or pooling funds. By enforcing transparency, approvals, and contribution-based limits, the system replace panic, guilt, and chaos
with clarity and speed during life-and-death situations. When time matters most, families decide faster, argue less, and act together.

<!-- 
[![Build Status](https://github.com/my-user/my-app/workflows/CI/badge.svg)](https://github.com/my-user/my-app/actions) -->
[![Code Coverage](https://codecov.io/gh/my-user/my-app/branch/main/graph/badge.svg)](https://codecov.io/gh/my-user/my-app)
[![License](https://img.shields.io/badge/License-CC%20BY--NC--ND%204.0-lightgrey.svg)](LICENSE)

## Table of Contents
* [Introduction](#introduction)
* [Rules](#rules)
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

### Rules
1. **Purpose**: Coordinate and enforce family emergency fund behavior during life-and-death-without holding money.
2. **Target Users**: Filipino families with multiple income earners, weak assurance coverage, and high exposure to medical emergencies.
3. **Group Structure**: Fixed groups (3-10 members).
4. **Contributions**: Fixed minimum monthly contribution set by group creator. Contributions are manually logged, and immutable.
5. **Transparency**: All contributors, missed payments, approvals, and withdrawals are visible to all group members.
6. **Emergency Types**: Hospitalization, Death, Burial, Calamity. Request outside these categories are not allowed.
7. **Withdrawal Thresholds**: Small (<= 10,000 PHP or 10% of committed funds), Medium (>Small to <= 50%), Large (>50%).
8. **Approval Rules**: Small auto-approved if eligible. Medium required 2-of-N approval. Large requires majority approval.
9. **Approval Window**: Time-bound (30-60minutes). Silence never equals consent.
10. **Fallback Behavior**: If approval expires, no withdrawal occurs. Request escalates to designated emergency approver(s).
11. **Withdrawal Cap Formula**: Maximum withdrawal equals the lesser of (lifetime contribution x 1.5) or total committed group funds.
12. **Cooldown Period**: 30 days per member after a successful withdrawal. Repeated withdrawals increase approval requirements.
13. **Non-Contributor Penalties**: 1 missed month = warning; 2 = loss of approval rights; 3 = withdrawal capped to small; 4 = auto-removal. (FOR R&D)
14. **Abuse Prevention**: Evidence required, permanent history, contribution-weighted rights, and visible trust records.
15. **System Role**: Coordinator and rules engine only. No custody of funds, no payment guarantees.

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
