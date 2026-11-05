# KnotBook 

*A little space for everything you make.*

KnotBook is a personal crochet project journal built with Android and Java Spring Boot. It allows users to record crochet projects, yarn materials, progress photos, crochet sessions and personal statistics.

## Tech Stack

### Backend
- Java 21
- Spring Boot 4.1.2
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- Flyway
- JWT Authentication
- BCrypt
- Maven
- Lombok
- JUnit 5 / Mockito

### Android (planned)
- Kotlin
- Jetpack Compose
- MVVM
- Retrofit
- Coroutines / Flow
- Hilt
- DataStore

## Current Backend Features

- User registration
- Email-based login
- BCrypt password hashing
- JWT access-token generation
- Refresh-token generation and rotation
- Refresh-token hashing and database persistence
- Logout and refresh-token revocation
- Request validation
- Global exception handling
- Stateless Spring Security configuration
- MySQL integration
- Flyway database migrations

## Project Structure

```text
com.knotbook.api
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── exception
├── repository
├── security
└── service
```

## Authentication Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | /api/auth/register | Register a user |
| POST | /api/auth/login | Login |
| POST | /api/auth/refresh | Refresh tokens |
| POST | /api/auth/logout | Revoke refresh token |

## Database

Database: `knotbook_db`

Current tables:
- `app_users`
- `refresh_tokens`
- `flyway_schema_history`

Database changes are managed through Flyway migrations. Hibernate uses `ddl-auto=validate`.

## Running Locally

1. Install Java 21 and MySQL.
2. Create the `knotbook_db` database.
3. Configure MySQL credentials.
4. Set the `JWT_SECRET` environment variable.
5. Open the project in IntelliJ IDEA.
6. Reload Maven dependencies.
7. Run `KnotbookApiApplication.java`.

Backend base URL:

`http://localhost:8080`

## Development Status

- [x] Spring Boot setup
- [x] MySQL configuration
- [x] Flyway setup
- [x] User entity and repository
- [x] Registration API
- [x] BCrypt password encoding
- [x] Login API
- [x] JWT access-token generation
- [x] Refresh-token management
- [x] Logout API
- [ ] Authentication security hardening and automated tests
- [ ] Android application setup
- [ ] Android authentication integration
- [ ] Project management APIs
- [ ] Materials APIs
- [ ] Progress journal APIs
- [ ] Work-session APIs

## Architecture Principles

- Layer-based package organization.
- DTOs for API requests and responses.
- Business logic inside service classes.
- Repository layer for database access.
- Stateless access-token authentication.
- Refresh tokens stored as hashes.
- Database changes managed using Flyway.
- Users can access only their own records.

More implementation details are available in the `docs/` directory.