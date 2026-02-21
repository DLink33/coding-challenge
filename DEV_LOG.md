# Backend Software Engineer Coding Challenge
### Notes Vault API

## LOG:

## Session 0 - Init Planning

### General Understanding  
Based on the provided document, this challenge is primarily about designing a simple backend API featuring user notes with three major components:

1. A server exposing several RESTful endpoints for creation, viewing, and deletion of notes  
2. A database layer that persists between separate server instances/runs  
3. Automated tests to prove critical behavior  

The Note data model must contain (at minimum):  
- `id` (UUID string)  
- `content` (UTF-8 string)  
- `created_at` (UTC timestamp as ISO-8601, e.g. `YYYY-MM-DDThh:mm:ssZ`)  

The process should be well documented (this log + README.md), and decisions/tradeoffs should be explicitly justified.  

---

### Language and Framework  
I will implement this in ***Java 21***. This will keep the solution modern, stable, and widely supported.  

Since this is a REST backend service, ***Spring Boot*** is a good fit to move quickly while keeping the structure maintainable. Spring Boot can be overblown, so I will aim to keep the setup minimal (REST controller + service + persistence).

---

### Build and Local Execution  
The requirement of “no external dependencies” appears to mean “no required external services.” Maven-managed dependencies are acceptable as long as the project runs locally with a straightforward workflow.  

Plan:  
- Use **Maven** with the **Maven Wrapper (`./mvnw`)** so the reviewer can build/run without having Maven installed.  
- Provide single-command options:  
  - `./mvnw spring-boot:run`  
  - `./mvnw test`  

A shaded/uber jar is optional and likely unnecessary given the wrapper-based workflow.

---

### Containerization  
The challenge encourages “single-command” execution; Docker is a reasonable option but not required. The Maven wrapper would suffice.  I will leave Docker containerization as a stretch goal.

Plan:  
- Prefer a clean local run via `./mvnw spring-boot:run`.  
- Optionally provide a **single Docker container** for convenience. Since SQLite is file-based, Docker networking complexity is not relevant. If Docker is included, mount the SQLite DB file via a bind mount/volume so data persists across container restarts.  

I will avoid Docker Compose unless it becomes necessary (it should not for SQLite).  

---

### Testing Approach  
I need at least:  
- **1+ API-level test**  
- **1+ data-layer or business-logic test**  

Spring Boot testing plan:  
- API test using **MockMvc** (validates status codes + response shape)  
- Data-layer test using **@DataJpaTest** (or a small unit test on the service layer if it better represents business logic)  

I’ll be pragmatic with TDD: focus on core behaviors and error cases without attempting exhaustive testing (100% code coverage etc.).  

---

### Database  
PostgreSQL and SQLite are both valid options; PostgreSQL feels heavy for the small scope of this task. I’ll use SQLite for a lightweight, file-backed database that still demonstrates real persistence.

- SQLite provides persistence across runs via a local `.db` file.  
- No need for multiple containers or external services (no Docker compose).
- Will need a Docker volume if containerized

---

### Version Control
Will be using Git/GitHub.
(Docker/DockerHub if my container is up to snuff)

### Dev Environment / IDE
Will be using RHEL9 distro on Windows Subsystem Linux
IDE will be VS Code connected to WSL
  - Extensions:
    - Extension Pack for Java (Microsoft)
    - Languages Support for Java (Red Hat)
    - Maven for Java
    - Project Manager for Java
    - Spring Boot Extension Pack

---

## MVP Design Summary

**Language/Framework**  
- Java 21 (LTS), Spring Boot (REST), Maven (wrapper included)  

**Persistence**  
- SQLite file-based DB (persists between runs)
  - Docker Volume if applicable

**API**  
- `POST /notes` - create note (returns created note with `id` + `created_at`)  
- `GET /notes` - list notes (**deterministic order: newest-first**)  
- `GET /notes/{id}` - fetch note  
- `DELETE /notes/{id}` - delete note (**404 if missing**)  

**Data Model**  
- `id` (UUID string)  
- `content` (required, UTF-8, trimmed)  
- `created_at` (UTC ISO-8601)  

**Containerization**  
- Optional Docker (single container); if used, persist SQLite DB via mounted file/volume
- I am going to aim for this.

**Testing**  
- 1+ API test using MockMvc  
- 1+ repository/service test using @DataJpaTest (or service unit test)  

**Run & Docs**  
- Run: `./mvnw spring-boot:run`  
- Tests: `./mvnw test`  
- README: run/test commands, curl examples, assumptions/tradeoffs, future improvements  

---

## Stretch Goals (Only if MVP is solid)
- Docker containerization 
- Search/filter/sort notes  
- Authentication  
- Simple front-end for manual testing  

---

## Session 1 - Project Setup & Environment Stabilization

### Objectives
- Initialize Spring Boot project
- Configure Maven and Java 21
- Establish SQLite persistence
- Ensure clean build + test cycle
- Normalize package structure

---

#### 1. Project Initialization
- Generated Spring Boot Maven project using Java 21 (LTS).
- Selected minimal dependencies:
  - Spring Web
  - Spring Data JPA
  - Validation
  - Spring Boot Test

#### 2. Dependency Configuration
- Added SQLite JDBC driver.
- Added `hibernate-community-dialects` to ensure compatibility with current Hibernate version.
- Removed incompatible third-party SQLite dialect dependency.

#### 3. Application Configuration
- Configured `application.properties` for:
  - SQLite datasource (`./data/notes.db`)
  - Hibernate community dialect
  - Schema auto-update
  - Explicit server port
- Created `data/` directory for DB persistence.
- Added SQLite DB files to `.gitignore`.

#### 4. Build Validation
- Verified:
  - `./mvnw clean test` passes
  - Application starts successfully via `./mvnw spring-boot:run`

---

### Observations

- SQLite + Hibernate compatibility required correct dialect dependency; mismatched dialect caused runtime `NoSuchMethodError`.
- Ensuring Maven runs under the intended JDK is critical when using `--release 21`.
- Cleaning build artifacts after package renaming prevents stale compiled classes from causing confusion.

---

### Status

Project scaffolding is stable.
Environment is reproducible.
Ready to begin implementation of core domain model (`Note`) and API endpoints.


## Session 2 - Building Data Model (Note Class)
### Objectives
- Add our Note class that will represent our user notes
  - id
  - content
  - timestamp
  - getters and setters

- Research and setup MVC with Springboot for a single endpoint

#### 1. Added NoteEntity
- Created NoteEntity class with decorators for a JPA entity

#### 2. Added NoteRepository Interface
- To simplify CRUD operations with database
- SQL not required for database queries and writes


### Observations
- NoteEntity must be formatted with JPA annotations to allow for proper interfacing with the database
- Since I am using a new major version (4.x.x) for springboot in the pom file, some imports and references have changed since 3.x.x

### Status and Next Steps

Project still buildable and stable. 
Will write test for database next


## Session 3 - Repository Test & Validation of Persistence Layer

### Objectives
- Implement a data-layer test for `NoteRepository`
- Confirm correct mapping of:
  - UUID primary key
  - `created_at` timestamp
  - trimmed content behavior


---

#### 1. Implemented `@DataJpaTest` for NoteRepository
- Created `NoteRepositoryTest`
- Used `@DataJpaTest` to load only the JPA slice of the application
- Activated `test` profile via `@ActiveProfiles("test")`
- Disabled automatic test database replacement to ensure SQLite is used:

  @AutoConfigureTestDatabase(replace = Replace.NONE)

This ensures the test runs against the same database technology (SQLite + Hibernate dialect) that the application will use in production.

---

#### 2. Configured Test-Specific Properties
- Added `src/test/resources/application-test.properties`
- Configured separate test database file (`notes-test.db`)
- Set:
  - `ddl-auto=create-drop` (clean schema per test run)
  - SQLite JDBC URL
  - Hibernate community SQLite dialect

This isolates test data from development data while still validating real persistence behavior.

---

#### 3. Verified Repository Round-Trip Behavior

The test confirms:

- A `NoteEntity` can be saved successfully
- UUID is generated
- `createdAt` timestamp is populated
- `content` is trimmed correctly
- Entity can be retrieved via `findById`

This validates:

- Entity-to-table mapping
- Dialect configuration
- Repository wiring
- Test profile configuration

---

### Observations

- Spring Boot 4.x reorganized test-related imports and requires technology-specific test starters (e.g., `spring-boot-starter-data-jpa-test`).
- `@DataJpaTest` attempts to replace the datasource by default; explicit configuration is required to test against SQLite.
- Testing against SQLite rather than an in-memory database reduces risk of dialect-related issues.

---

### Status

Data layer is now verified and stable.

The following are confirmed working:

- Hibernate + SQLite dialect
- JPA entity mapping
- Spring Data repository integration
- Isolated test profile

---

### Next Steps

- Implement first REST endpoint (`POST /notes`)
- Add API-level test using `MockMvc`
- Validate:
  - 201 response on success
  - 400 response for blank content
  - Proper JSON structure in response body


## Session 4 - POST /notes Endpoint & API-Level Testing

### Objectives
- Implement `POST /notes`
- Introduce API-level test using `MockMvc`
- Return proper `201 Created` response
- Validate `400 Bad Request` for blank content

---

#### 1. Resolved Spring Boot 4 Test Configuration Changes
- `@AutoConfigureMockMvc` package moved in Boot 4.x
- Updated import to:

  org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc

- Added `spring-boot-starter-webmvc-test` (test scope)

This restored proper `MockMvc` support under Boot 4.x.

---

#### 2. Implemented API-Level Test (`NoteControllerTest`)
- Used `@SpringBootTest`
- Used `@AutoConfigureMockMvc`
- Injected `MockMvc`
- Wrote tests to validate:

  - `201 Created` on valid request
  - `Location` header is set
  - JSON response contains:
    - `id`
    - `content`
    - `createdAt`
  - `400 Bad Request` when content is blank

Initial test failure returned `404`, confirming endpoint did not yet exist.

---

#### 3. Implemented Minimal `POST /notes` Controller

Created:

- `CreateNoteRequest` (request DTO with `@NotBlank`)
- `NoteResponse` (response DTO)
- `NoteController`

Controller behavior:

- `@RequestBody` binds JSON → DTO
- `@Valid` triggers Bean Validation
- `ResponseEntity.created(...).body(...)` returns:
  - HTTP 201
  - `Location` header
  - JSON response

Temporary UUID + `Instant.now()` used as stub persistence (to be replaced by service/repository layer).

---

#### 4. Verified Request Lifecycle Behavior

Observed during test execution:

- Blank content triggers `MethodArgumentNotValidException`
- Spring returns `400 Bad Request`
- Controller method body does not execute on validation failure
- Successful requests return `201` and expected JSON structure

This confirms:

- Jackson JSON binding is working
- Validation pipeline is functioning
- Proper HTTP semantics are enforced

---

### Observations

- Spring Boot 4.x modularized test auto-configuration and moved `MockMvc`-related annotations.
- Validation occurs before controller logic executes.
- `@RestController` automatically serializes return values via Jackson.
- `ResponseEntity.created()` properly sets `201 Created` and `Location` header.

---

### Status

API layer now supports:

- `POST /notes`
- Proper 201 response semantics
- Validation-driven 400 behavior
- API-level integration testing via `MockMvc`

---

### Next Steps

- Replace stub UUID/timestamp with real service + repository persistence
- Decide on structured error body (`@RestControllerAdvice`) vs status-only 400
- Implement `GET /notes`
- Implement `GET /notes/{id}`


## Session 5 - Introduce Service Layer & Refactor Creation Logic

### Objectives
- Introduce `NoteService` between `NoteController` and `NoteRepository`
- Separate HTTP, business, and persistence responsibilities
- Remove stub persistence from controller
- Move normalization and creation logic out of `NoteEntity`
- Add focused unit test for `NoteService`

---

### Completed

#### 1. Created `NoteService`
- Added `NoteService` annotated with `@Service`
- Injected `NoteRepository` via constructor injection
- Moved note creation responsibilities into service:
  - Trim incoming content
  - Guard against blank/null content (defensive validation)
  - Generate UUID
  - Set `createdAt` timestamp
  - Persist via `noteRepository.save(...)`
- Returned persisted `NoteEntity` to controller

This establishes a clear separation of concerns:

- **Controller** → HTTP semantics (status codes, headers, DTO mapping)
- **Service** → Business logic and entity conditioning
- **Repository** → Persistence
- **Entity** → Pure JPA-mapped data model

---

#### 2. Refactored `NoteController`
- Removed stub UUID/timestamp generation
- Delegated creation to `NoteService`
- Mapped saved entity → `NoteResponse`
- Preserved REST semantics:
  - `201 Created`
  - `Location` header
  - Response body with `id`, `createdAt`, `content`

Controller now acts strictly as an HTTP adapter.

---

#### 3. Refactored `NoteEntity`
- Removed content-based constructor
- Removed trimming logic from entity
- Added setters for `id` and `createdAt`
- Entity now functions strictly as a persistence model

This prevents business rules from leaking into the persistence layer.

---

#### 4. Updated `NoteRepositoryTest`
- Adjusted test to manually assign required fields (`id`, `createdAt`) before saving
- Clarified that repository tests validate mapping behavior only
- Removed reliance on entity-side creation logic

---

#### 5. Added `NoteServiceTest` (Unit Test with Mockito)
- Used `@ExtendWith(MockitoExtension.class)`
- Mocked `NoteRepository`
- Verified:
  - Content trimming occurs
  - UUID and `createdAt` are assigned
  - `noteRepository.save()` is called exactly once
  - Blank/null input throws `IllegalArgumentException`
  - Repository is not called when validation fails

This test validates business logic independently from JPA or HTTP layers.

---

### Observations

- Separating service logic clarified responsibility boundaries significantly.
- Repository tests should validate persistence behavior only — not business rules.
- Unit testing service logic with Mockito is much simpler and faster than using full Spring context.
- Constructor injection + final fields enforce immutability and clearer wiring.
- Refactor required manual ID assignment in repository tests due to removal of entity constructor.

---

### Status

- Project builds successfully
- All tests pass
- Clear architectural separation established:
  - Controller → Service → Repository

---

### Next Steps

- Strengthen `NoteControllerTest` with persistence assertion
- Implement `GET /notes/{id}`
- Implement `GET /notes`
- Decide on final error response shape for invalid requests
