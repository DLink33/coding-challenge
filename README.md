# Notes Vault API

Backend coding challenge implementation for Bluestaq.

This project implements a minimal REST API for creating, retrieving, listing, and deleting notes. The focus of this implementation is clarity, maintainability, and explicit tradeoffs rather than feature completeness.

---

## Tech Stack

- Java 21 (LTS)
- Spring Boot
- Spring Data JPA (Hibernate)
- SQLite (file-based persistence)
- Maven (with Maven Wrapper)

---

## Running the Application

From the project directory:

```bash
cd notesvault
./mvnw spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## Running Tests

```bash
cd notesvault
./mvnw test
```

---

## Persistence

Data is persisted to a local SQLite database file:

```
notesvault/data/notes.db
```

This file is ignored by git and allows data to persist between application runs.

---

## Architectural Overview

The application is structured using standard Spring Boot layering:

- Controller (REST endpoints)
- Service (business logic)
- Repository (data access via Spring Data JPA)
- Entity (JPA-mapped database model)

SQLite is used as a lightweight embedded relational database. Hibernate’s community SQLite dialect is used to ensure SQL compatibility.
