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

SQLite was selected because it satisfies the persistence requirement without requiring external infrastructure. It keeps the project simple to run locally while still demonstrating relational database usage through JPA.

Limitations:
- SQLite is not intended for high-concurrency production workloads.
- Migrating to PostgreSQL or another relational database would primarily require configuration and dependency changes.

---

## Architectural Overview

The application is structured using standard Spring Boot layering:

- Controller (REST endpoints)
- Service (business logic)
- Repository (data access via Spring Data JPA)
- Entity (JPA-mapped database model)

SQLite is used as a lightweight embedded relational database. Hibernate’s community SQLite dialect is used to ensure SQL compatibility.

Business logic such as validation, trimming, ordering, and delete behavior resides in the Service layer. Controllers are intentionally kept thin and focused on HTTP concerns.

---

## API Endpoints

**The current API version  is v1**

### POST /v1/notes
Create a new note.

Request body:

```json
{
  "content": "My first note"
}
```

Response: 
```
201 Created
```

```json
{
  "id": "generated-uuid",
  "content": "My first note",
  "createdAt": "2026-02-22T23:41:12Z"
}
```

Behavior:
- Content must not be null or blank.
- Content is trimmed before persistence.
- Returns `400 Bad Request` for invalid input.

---

### GET /v1/notes
List all notes.

Response: `200 OK`

```json
[
  {
    "id": "...",
    "content": "...",
    "createdAt": "..."
  }
]
```

Notes are returned in newest-first order.

---

### GET /v1/notes/{id}
Retrieve a single note by ID.

Response:
```
200 OK
```
If the note does not exist:

```
404 Not Found
```

---

### DELETE /v1/notes/{id}
Delete a note by ID.

Response if successful:

```
204 No Content
```

If the note does not exist:

```
404 Not Found
```

Deletion logic is handled in the Service layer. A missing note results in a `NoteNotFoundException`, which is translated to a `404` response.

---

### PUT /v1/notes/{id}
Update a note by ID.

Request body:

{
  "content": "Updated note content"
}

Response if successful:

```
200 OK
```

{
  "id": "existing-uuid",
  "content": "Updated note content",
  "createdAt": "2026-02-22T23:41:12Z"
}

If the note does not exist:

```
404 Not Found
```

If the content is null or blank:

```
400 Bad Request
```
## Testing

The project includes:

- API-level tests using MockMvc
- Service-layer unit tests using Mockito

Service tests validate:
- Content validation rules
- Trimming behavior
- Retrieval behavior
- Ordering logic
- Delete behavior (including exception handling)

Tests are designed to be fast, focused, and deterministic.

---

## Assumptions & Tradeoffs

- Authentication and authorization are out of scope
- Pagination is not required for the MVP; GET notes/ returns everything in db
- Uses SQLite versus something like Postgresql
- No containerization

implementation emphasizes clarity, layering, and predictable API 

---

## Build & Submission

The project runs locally with a single command using the Maven Wrapper:

```bash
cd notesvault
./mvnw spring-boot:run
```
