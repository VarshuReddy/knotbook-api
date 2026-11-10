# KnotBook

*A little space for everything you make.*

KnotBook is a personal crochet project journal built with **Android (Kotlin) and Java Spring Boot**.

It helps users organize their crochet projects, record yarn and hook details, upload progress photos, maintain project notes, track crafting time and view their project statistics.

KnotBook is designed as a personal creative journal, not a social networking or marketplace application.

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.1.2
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- Flyway
- JWT Authentication
- BCrypt Password Encoding
- Maven
- Lombok
- ImageKit (Image Storage)
- JUnit 5 / Mockito

### Android

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Retrofit
- Coroutines / Flow
- Hilt
- DataStore

---

## Backend Features

### 1. Authentication and Authorization

- User registration
- Email-based login
- BCrypt password hashing
- JWT access-token generation
- Refresh-token generation and rotation
- Secure refresh-token hashing and database persistence
- Refresh-token revocation on logout
- Stateless Spring Security configuration
- Protected API endpoints

### 2. Project Management

- Create crochet projects
- Retrieve all user projects
- Retrieve individual project details
- Update project information
- Soft-delete projects
- Search projects by title
- Filter projects by status
- Paginated project listing

Supported project statuses:

- `PLANNING`
- `IN_PROGRESS`
- `COMPLETED`
- `ON_HOLD`

Project information includes:

- Project title and description
- Category and status
- Start and end dates
- Yarn details (color, weight and type)
- Crochet hook sizes
- Total crafting time

Yarn and hook information is currently stored specifically for each project rather than maintained as a separate inventory.

### 3. Project Photos

- Upload project photos through the backend
- Store images using ImageKit
- Save image metadata and URLs in MySQL
- Retrieve project photos
- Delete project photos
- Select and update the project cover photo
- Support image captions and photo dates

Image files are stored in ImageKit. The database stores their associated metadata, including image URL, ImageKit file ID and file path.

### 4. Project Notes

- Create or update project notes
- Retrieve project notes
- Delete project notes
- Support multiline text, punctuation and emoji

Each project has one personal notes document.

### 5. Crafting Time Tracking

- Manually add crafting time to a project
- Store accumulated time in minutes
- Retrieve total crafting time with project details
- Include total crafting time in dashboard statistics

Time is added by the user rather than automatically measured through a timer or session-tracking system.

### 6. Home Dashboard

- Retrieve total project count
- Retrieve in-progress project count
- Retrieve completed project count
- Retrieve accumulated crafting time
- Display recently updated projects
- Include project cover images in recent project results

### 7. API Validation and Exception Handling

- Request validation using Jakarta Bean Validation
- Centralized global exception handling
- Consistent API error responses
- Authentication and authorization error handling
- Resource ownership validation

---

## API Endpoints

All protected endpoints require a valid JWT access token in the Authorization header.

Format:

`Authorization: Bearer <access_token>`

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Authenticate a user |
| POST | `/api/auth/refresh` | Refresh access and refresh tokens |
| POST | `/api/auth/logout` | Revoke refresh token |

### Projects

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | Retrieve paginated projects |
| GET | `/api/projects/{projectId}` | Retrieve project details |
| PUT | `/api/projects/{projectId}` | Update project details |
| DELETE | `/api/projects/{projectId}` | Soft-delete a project |
| POST | `/api/projects/{projectId}/time` | Add crafting time |

Project listing supports:

- `page`
- `size`
- `search`
- `status`

Example:

`GET /api/projects?page=0&size=10&search=cardigan&status=IN_PROGRESS`

### Project Photos

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/projects/{projectId}/photos` | Upload a photo |
| GET | `/api/projects/{projectId}/photos` | Retrieve project photos |
| DELETE | `/api/projects/{projectId}/photos/{photoId}` | Delete a photo |
| PATCH | `/api/projects/{projectId}/photos/{photoId}/cover` | Set project cover photo |

Photo uploads use multipart form data.

### Project Notes

| Method | Endpoint | Purpose |
|---|---|---|
| PUT | `/api/projects/{projectId}/notes` | Create or update notes |
| GET | `/api/projects/{projectId}/notes` | Retrieve notes |
| DELETE | `/api/projects/{projectId}/notes` | Delete notes |

### Dashboard

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/dashboard` | Retrieve home dashboard statistics and recent projects |

---

## Project Structure

The backend follows a layer-based package organization.

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

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `config` | Application and infrastructure configuration |
| `controller` | REST API endpoints |
| `dto` | Request and response objects |
| `entity` | JPA database entities |
| `exception` | Custom exceptions and global exception handling |
| `repository` | Database operations using Spring Data JPA |
| `security` | JWT generation, validation and security-related components |
| `service` | Application business logic |

---

## Database

**Database:** `knotbook_db`

### Main Tables

| Table | Purpose |
|---|---|
| `app_users` | User account information |
| `refresh_tokens` | Hashed refresh-token storage |
| `projects` | Crochet project information |
| `project_yarns` | Yarn details associated with projects |
| `project_hooks` | Hook sizes associated with projects |
| `project_photos` | Project photo metadata and ImageKit references |
| `project_notes` | Personal notes associated with projects |

`flyway_schema_history` is maintained automatically by Flyway.

### Database Relationships

- One user can have multiple projects.
- One project can have multiple yarn entries.
- One project can have multiple hook entries.
- One project can have multiple photos.
- One project can have one notes document.
- One user can have multiple refresh tokens.

### Database Management

- Database schema changes are managed through Flyway migrations.
- Hibernate uses `ddl-auto=validate`.
- Foreign keys enforce relational integrity.
- Project ownership is enforced at the service layer.
- Projects use soft deletion through `is_deleted` and `deleted_at`.
- Project child records are retained when a project is soft-deleted.

---

## Authentication Architecture

KnotBook uses stateless JWT-based authentication.

### Access Token

- Signed using HS256.
- Contains the authenticated user's ID as the JWT subject.
- Includes the user's email and token metadata.
- Expires after 15 minutes.

### Refresh Token

- Generated using a cryptographically secure random value.
- Expires after 30 days.
- Stored in the database as a SHA-256 hash.
- Rotated whenever a refresh request succeeds.
- Revoked when the user logs out.

### Security

- Passwords are hashed using BCrypt.
- Access tokens are validated through Spring Security's OAuth2 Resource Server JWT support.
- Protected endpoints derive the user ID from the authenticated JWT.
- Users cannot access another user's project resources.

---

## Image Storage Architecture

Project photos are uploaded through the Spring Boot backend.

```text
Android Application
        |
        | Multipart Image Upload
        v
Spring Boot Backend
        |
        | Upload Image
        v
ImageKit
        |
        | Image URL + File ID + File Path
        v
Spring Boot Backend
        |
        | Save Image Metadata
        v
MySQL Database
        |
        | Return Image URL
        v
Android Application
```

Image files are stored in ImageKit rather than directly in MySQL.

The backend handles image uploads, metadata persistence and deletion.

ImageKit private credentials are configured through environment variables and must never be committed to source control.

---

## Running Locally

### Prerequisites

- Java 21
- Maven
- MySQL
- IntelliJ IDEA (recommended)
- ImageKit account and API credentials

### 1. Clone the Repository

```bash
git clone <repository-url>
cd knotbook-api
```

### 2. Create the Database

```sql
CREATE DATABASE knotbook_db;
```

### 3. Configure Environment Variables

Configure the following environment variables in your IDE run configuration:

```properties
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_generated_jwt_secret

IMAGEKIT_PUBLIC_KEY=your_imagekit_public_key
IMAGEKIT_PRIVATE_KEY=your_imagekit_private_key
```

Use your own credentials. Do not commit environment variables or private keys to Git.

### 4. Verify Application Configuration

The backend uses environment variables in `application.properties`:

```properties
spring.application.name=knotbook-api

spring.datasource.url=jdbc:mysql://localhost:3306/knotbook_db
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

security.jwt.secret=${JWT_SECRET}
security.jwt.issuer=knotbook-api
security.jwt.access-expiration=900
security.jwt.refresh-expiration=2592000

imagekit.public-key=${IMAGEKIT_PUBLIC_KEY}
imagekit.private-key=${IMAGEKIT_PRIVATE_KEY}
```

### 5. Run the Application

- Open the project in IntelliJ IDEA.
- Reload Maven dependencies.
- Verify MySQL is running.
- Configure the required environment variables.
- Run `KnotbookApiApplication.java`.

Flyway automatically applies pending database migrations when the application starts.

### 6. Backend Base URL

```text
http://localhost:8080
```

For Android Emulator access:

```text
http://10.0.2.2:8080/
```

---


## Architecture Principles

- Layer-based package organization.
- DTOs for API requests and responses.
- Business logic maintained in service classes.
- Database access handled through repositories.
- Stateless JWT-based authentication.
- Refresh tokens stored as hashes.
- Database migrations managed through Flyway.
- User ownership verified for protected resources.
- Project deletion handled using soft deletion.
- Image files stored externally using ImageKit.
- Transactional service methods for multi-step database operations.

---

## Documentation

Additional implementation notes and technical decisions are maintained in the `docs/` directory.

These documents cover authentication, JWT implementation, database design, API architecture and other important development decisions.

---

**KnotBook:** A little space for everything you make. 🧶