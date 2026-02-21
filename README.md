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

---

# Spring Boot Annotation Reference (Notes Vault)

This project intentionally uses a small, focused subset of Spring Boot annotations. Below is a practical reference for what each one does in this application.

---

## 🌐 Controller Layer (HTTP API)

### `@RestController`
Marks the class as a REST controller.

- Combines `@Controller` + `@ResponseBody`
- Return values are automatically serialized to JSON

---

### `@RequestMapping("/notes")`
Defines a base URL path for all endpoints in the controller.

Example:
```java
@RequestMapping("/notes")
```

All methods inside map under `/notes`.

---

### `@PostMapping`
Maps HTTP POST requests to a method.

Example:
```java
@PostMapping
```

With class-level `/notes`, this handles:

```
POST /notes
```

---

### `@RequestBody`
Tells Spring to deserialize JSON from the request body into a Java object.

Example:
```java
public ResponseEntity<?> create(@RequestBody CreateNoteRequest req)
```

Under the hood:
- Jackson parses JSON
- Instantiates `CreateNoteRequest`
- Passes it into the method

---

### `ResponseEntity`
Used to control:
- HTTP status code
- Headers
- Response body

Example:
```java
return ResponseEntity
    .created(location)
    .body(response);
```

Returns:
- `201 Created`
- `Location` header
- JSON body

---

## ✅ Validation

### `@Valid`
Triggers bean validation before controller logic runs.

If validation fails:
- Method does not execute
- Spring returns `400 Bad Request`

---

### `@NotBlank`
Validation constraint for Strings.

```java
@NotBlank
String content;
```

Ensures:
- Not null
- Not empty
- Not only whitespace

---

## 🧱 DTOs (Data Transfer Objects)

Used to separate API contracts from persistence models.

### Request DTO
```java
public record CreateNoteRequest(
    @NotBlank String content
) {}
```

Represents input shape.

---

### Response DTO
```java
public record NoteResponse(
    String id,
    String content,
    Instant createdAt
) {}
```

Represents output shape.

Spring automatically serializes this to JSON.

---

## 🗄 Persistence (JPA)

### `@Entity`
Marks a class as a database-mapped entity.

### `@Id`
Marks the primary key field.

Spring Data repositories provide CRUD behavior without manual SQL.

---

## 🧪 Testing

### `@SpringBootTest`
Bootstraps the full application context for integration testing.

---

### `@AutoConfigureMockMvc`
Provides a `MockMvc` instance for testing HTTP endpoints without running a real server.

(Boot 4 package:)
```java
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
```

---

### `MockMvc`
Used to simulate HTTP requests in tests.

Example:
```java
mockMvc.perform(post("/notes")
    .contentType(MediaType.APPLICATION_JSON)
    .content(json))
  .andExpect(status().isCreated());
```

---

## 🔄 Request Lifecycle (High-Level)

```
HTTP Request
   ↓
Route matched (@PostMapping)
   ↓
JSON → DTO (@RequestBody)
   ↓
Validation (@Valid + @NotBlank)
   ↓
Controller logic
   ↓
ResponseEntity returned
   ↓
DTO → JSON (Jackson serialization)
```

---

## Design Philosophy for This Project

This implementation aims to be:

- Minimal
- Professional
- Test-driven
- Clear separation of API contract and persistence model
- Explicit about HTTP semantics (201 vs 400)

