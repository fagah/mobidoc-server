# MobiDoc Server

Backend monolithique pour MobiDoc avec architecture clean, tests unitaires, Flyway et Keycloak.

## Technologies utilisées

- Spring Boot 3.2.2
- Java 17
- PostgreSQL
- Flyway
- Keycloak
- JaCoCo (90% coverage)
- MapStruct
- Lombok

## Structure du projet

Le projet suit une architecture clean avec les couches suivantes :

- Domain
- Application
- Infrastructure
- API

## Configuration requise

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Keycloak 23+

## Installation

1. Cloner le repository
2. Configurer la base de données PostgreSQL
3. Configurer Keycloak
4. Lancer l'application avec `mvn spring-boot:run`