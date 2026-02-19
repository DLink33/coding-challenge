# Backend Software Engineer Coding Challenge
### Notes Vault API

---

# LOG:

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
The challenge encourages “single-command” execution; Docker is a reasonable option but not required. The Maven wrapper would suffice. 

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

I’ll be pragmatice with TDD: focus on core behaviors and error cases without attempting exhaustive testing (100% code coverage etc.).  

---

### Database  
PostgreSQL and SQLite are both valid options; PostgreSQL feels heavy for the small scope of this task. I’ll use SQLite for a lightweight, file-backed database that still demonstrates real persistence.

- SQLite provides persistence across runs via a local `.db` file.  
- No need for multiple containers or external services (no Docker compose).

---

### Version Control
Will be using Git/GitHub.
Docker/DockerHub if my container is up to snuff

---

## MVP Design Summary

**Language/Framework**  
- Java 21 (LTS), Spring Boot (REST), Maven (wrapper included)  

**Persistence**  
- SQLite file-based DB (persists between runs)  

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
- Search/filter/sort notes  
- Authentication  
- Simple front-end for manual testing  

---

## Session 1 - Setup
### Goals:
- Initialize git repo with main and short-lived dev branch (keep main always runnable).
- Add .gitignore for Java/Maven/IDE + data/*.db.
- Create project via Spring Initializr (Java 21, Maven, Spring Boot).
- Dependencies: Spring Web, Spring Data JPA, Spring Boot Test (+ Validation optional).
- Add SQLite JDBC driver in pom.xml.
- Establish DB file location early (./data/notes.db) to support persistence and easy Docker mounting later.
- Decide Docker as optional: include a single-container Dockerfile only if time permits, mounting ./data for persistence.

