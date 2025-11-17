# OreoPetBath

A modern full-stack application for a pet grooming & boarding shop with comprehensive booking management, conflict detection, and concurrent access protection.

**Customer (PUBLIC)** can browse and book without login

**Staff (OWNER)** can sign in to a protected dashboard to manage reservations

## Overview

- **Public site**: Home, Service, Pricing, Gallery, Reservation (no login needed)
- **Owner Dashboard**: Auto-refresh Live Table, Multi-key Sorting (Date → Time → Status), Status Update, "Complete" action, Special Note Display
- **Booking rules**: Time-window Conflict Detection for services like Grooming/Washing; **Boarding never conflicts** with other services
- **Concurrency protection**: Transactional isolation, pessimistic locking, and optimistic locking to prevent race conditions
- **Comprehensive testing**: Full unit test suite covering all services, controllers, and exception handlers

---

## Architecture & Stack

```
React + Vite (Tailwind)  →  Spring Boot 3 (JWT, JPA)  →  PostgreSQL 16
```

- **Frontend**: React 18, Vite 7, Tailwind CSS
- **Backend**: Spring Boot 3.5.4, Java 17, Spring Security (JWT), Spring Data JPA, Flyway
- **Database**: PostgreSQL 16
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Dev runtime**: Docker Compose (frontend, backend, db)

---

## Project Structure

```
infra/
  docker-compose.yml          # dev stack: frontend, backend, db
frontend/
  src/
    App.jsx
    OwnerDashboard.jsx
    auth/
      AuthContext.jsx           # provides { user, token, login(), logout() }
      useAuth.jsx               # hook to access AuthContext
    components/
      Header.jsx
      Footer.jsx
      ServiceCard.jsx
    lib/
      api.js                    # apifetch
      galleryData.js            # use to process gallery photos
    pages/
      Gallery.jsx
      Home.jsx
      Login.jsx
      OwnerDashboard.jsx
      Pricing.jsx
      Reservation.jsx           # public booking page
      Services.jsx
    routes/
      RoleRoute.jsx             # guard routes by role; redirects to /login
backend/
  src/main/java/com/example/demo/
    auth/                       # AuthController, JwtService, User, UserRepo
    config/                     # BookingConfig, DataSeed, ReservationProps, SecurityConfig, WebConfig
    controller/                 # ReservationController, PetController, ServiceOfferingController
    dto/                        # ReservationRequest, ReservationDTO, ServiceOfferingDTO, ServiceOption
    exception/                  # BookingConflictException
    handler/                    # GlobalExceptionHandler
    model/                      # Reservation, Pet, ServiceOffering, ReservationStatus
    repo/                       # ReservationRepo, PetRepo, ServiceOfferingRepo
    service/                    # ReservationService, PetService, ServiceOfferingService
  src/main/resources/
    application.properties      # JWT + DB config via env overrides
    application-local.properties # Local development config (localhost DB)
    db/migration/
      V1__init.sql              # Initial schema: pet, users, service, reservation
      V2__add_version_to_reservation.sql  # Adds version column for optimistic locking
  src/test/java/com/example/demo/
    auth/                       # JwtServiceTest
    controller/                 # PetControllerTest, ReservationControllerTest,
                                # ServiceOfferingControllerTest, AuthControllerTest
    handler/                    # GlobalExceptionHandlerTest
    service/                    # PetServiceTest, ReservationServiceTest,
                                # ServiceOfferingServiceTest
```

---

## Configuration (env → properties)

**JWT**

```properties
# src/main/resources/application.properties
app.jwt.secret=${APP_JWT_SECRET:tlymOFHvwIjEYg+DD37UMro/K9qqdhKbkgT3D1rwtb/Mrzi+6lG2VRWnbmphLlX7s}
app.jwt.ttlMillis=${APP_JWT_TTLMILLIS:43200000} # default 12 hours
```

**Reservation Buffer**

```properties
reservation.buffer-minutes=29  # Time buffer for conflict detection (in minutes)
```

**Database (Local Development)**

```properties
# src/main/resources/application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/oreopet
spring.datasource.username=oreo
spring.datasource.password=oreo
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.initialization-fail-timeout=60000
```

---

## Frontend

### Routing

- **Public**: `/`, `/service`, `/pricing`, `/gallery`, `/reservation`
- **Auth**: `/login` (submits credentials, stores `{jwt, user:{username,role}}`)
- **Protected**: `/owner` (wrapped in `<RoleRoute roles={["OWNER"]}>…</RoleRoute>`)

### Owner Dashboard (key behaviors)

- **Fetching**: `GET /api/reservations/dashboard?phone=&date=&ts=<ms>` (no-cache)
- **Multi-sort**: Date (primary) → Time (secondary) → Status (tertiary)
- **Status colors**:
  `BOOKED` → blue, `CANCELED` → yellow, `COMPLETED` → green
- **Actions**:
  "Complete" → `PATCH /api/reservations/{id}/complete`, then re-fetch list
- **Notes**: Truncated text opens a **scrollable modal**; close button pinned and always visible
- **Auto-refresh**: periodic re-fetch + re-fetch on state-changing actions

---

## Backend

### Security (summary)

- **Public** endpoints back the public pages
- **Owner** endpoints require `Authorization: Bearer <JWT>` with role `OWNER`
- `POST /api/auth/login` is public and returns `{token, username, role}`

> Your `SecurityConfig` explicitly `permitAll()` for the public paths/APIs used by `Reservation.jsx`, and requires `hasRole("OWNER")` for the dashboard APIs used by `OwnerDashboard.jsx`

### Reservation Rules & Concurrency Protection

#### Conflict Detection

- **Conflict window**: for requested time **T**, check `[T - buffer, T + buffer]` where `buffer = ReservationProps.bufferMinutes()` (default: 29 minutes)
- **Boarding exemption**: **boarding never conflicts** with other services and isn't blocked by them
  - Implemented via a derived query that ignores reservations whose `service.code == "BOARD"` when checking for clashes

#### Race Condition Protection

The system implements multiple layers of protection against concurrent access issues:

1. **Transactional Isolation** (`SERIALIZABLE`)

   - Reservation creation uses `@Transactional(isolation = Isolation.SERIALIZABLE)` to ensure atomic check-and-save operations
   - Prevents phantom reads and ensures no overlapping reservations can be created

2. **Pessimistic Locking**

   - Conflict check query uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` to lock rows during conflict detection
   - Status update operations (`cancel()`, `complete()`) use `findByIdWithLock()` to prevent lost updates

3. **Optimistic Locking**

   - `Reservation` entity includes `@Version` field for automatic version tracking
   - JPA automatically detects concurrent modifications and throws `OptimisticLockException` if conflicts occur

4. **Database Migration**
   - `V2__add_version_to_reservation.sql` adds the `version` column with default value 0
   - Existing reservations automatically get version = 0, new reservations auto-increment

**Result**: The system safely handles concurrent booking requests without creating overlapping reservations or losing status updates.

### REST API

#### Authentication

- `POST /api/auth/login` - Login with username/password, returns JWT token

#### Pets (Public)

- `GET /api/pets` - Get all pets
- `GET /api/pets/{id}` - Get pet by ID
- `POST /api/pets` - Create new pet (public)
- `PUT /api/pets/{id}` - Update pet
- `DELETE /api/pets/{id}` - Delete pet

#### Reservations

**Public Endpoints:**

- `POST /api/reservations` - Create reservation (with conflict checking)
- `GET /api/reservations` - Get all reservations
- `GET /api/reservations/date?d={date}` - Get reservations by date
- `GET /api/reservations/pet/{petId}` - Get reservations by pet ID
- `PATCH /api/reservations/{id}/cancel` - Cancel reservation (requires phone verification)

**Owner-Only Endpoints:**

- `GET /api/reservations/dashboard?phone=&date=` - Get filtered reservations for dashboard
- `PATCH /api/reservations/{id}/complete` - Mark reservation as completed

#### Services

- `GET /api/services` - Get all service offerings
- `GET /api/services/{id}` - Get service offering by ID

### Exception Handling

- `BookingConflictException` → HTTP 409 (Conflict)
- `EntityNotFoundException` → HTTP 404 (Not Found)
- Generic exceptions → HTTP 500 (Internal Server Error)

All exceptions return JSON with `{timestamp, status, error, message}` format.

---

## Testing

### Unit Test Suite

The project includes comprehensive unit tests covering all major functionalities:

**Test Coverage:**

- ✅ **61 test cases** (60 passing, 1 skipped)
- ✅ Service layer tests (business logic)
- ✅ Controller layer tests (REST endpoints)
- ✅ Exception handler tests
- ✅ Authentication tests

**Test Files:**

```
backend/src/test/java/com/example/demo/
├── auth/
│   └── JwtServiceTest.java              # JWT token generation and parsing
├── controller/
│   ├── AuthControllerTest.java          # Login endpoint tests
│   ├── PetControllerTest.java           # Pet CRUD endpoint tests
│   ├── ReservationControllerTest.java   # Reservation endpoint tests
│   └── ServiceOfferingControllerTest.java # Service endpoint tests
├── handler/
│   └── GlobalExceptionHandlerTest.java   # Exception handling tests
└── service/
    ├── PetServiceTest.java               # Pet service business logic
    ├── ReservationServiceTest.java       # Reservation service with conflict checking
    └── ServiceOfferingServiceTest.java   # Service offering business logic
```

### Running Tests

```bash
cd backend

# Run all tests
./gradlew test

# Run with verbose output (shows test results like pytest/JUnit)
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.demo.service.ReservationServiceTest"

# Clean and test
./gradlew clean test
```

**Test Output Format:**

```
PetServiceTest > testSave_ShouldReturnSavedPet() PASSED
ReservationServiceTest > testCreate_WhenNoConflict_ShouldCreateReservation() PASSED
...

======================================================
|  SUCCESS (61 tests, 60 passed, 0 failed, 1 skipped)  |
======================================================
```

### Test Configuration

Tests are configured with:

- Mockito for dependency mocking
- Spring Boot Test for integration testing
- Security auto-configuration disabled for controller tests
- H2 in-memory database for exception handler tests

---

## Database Migrations

The project uses Flyway for database version control:

### V1\_\_init.sql

- Creates `pet` table
- Creates `users` table
- Creates `service` table with initial data (BOARD, GROOM, WASH)
- Creates `reservation` table with indexes

### V2\_\_add_version_to_reservation.sql

- Adds `version` column to `reservation` table for optimistic locking
- Creates index on `version` column
- Sets default value to 0 for existing records

Migrations run automatically on application startup.

---

## How to Run (Development)

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Node.js 18+ (for frontend development)

### Option 1: Docker Compose (Full Stack)

```bash
# Start all services (frontend, backend, database)
cd infra
docker-compose up --build

# Frontend: http://localhost:5173
# Backend : http://localhost:8080
```

### Option 2: Local Development (Backend Only)

```bash
# 1. Start database
cd infra
docker-compose up -d db

# Wait 10-15 seconds for database to initialize

# 2. Start backend (uses local profile)
cd ../backend
./gradlew bootRun --args='--spring.profiles.active=local'

# Backend: http://localhost:8080
```

### Option 3: Manual Database Setup

If you have PostgreSQL running locally:

```bash
# Create database and user
createdb oreopet
createuser oreo
psql -d oreopet -c "ALTER USER oreo WITH PASSWORD 'oreo';"
psql -d oreopet -c "GRANT ALL PRIVILEGES ON DATABASE oreopet TO oreo;"

# Start backend
cd backend
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Running Tests

```bash
cd backend

# Run all tests
./gradlew test

# Run tests with verbose output
./gradlew test

# View test report
open build/reports/tests/test/index.html
```

---

## Key Features

### 1. Concurrent Booking Protection

- Prevents double-booking through transactional isolation and pessimistic locking
- Optimistic locking detects concurrent modifications
- Safe for high-traffic scenarios

### 2. Conflict Detection

- Automatic time-window conflict checking for grooming/washing services
- Boarding services never conflict with other services
- Configurable buffer time (default: 29 minutes)

### 3. Comprehensive Testing

- 60+ unit tests covering all business logic
- Controller tests for all REST endpoints
- Exception handling tests
- Race condition scenario tests

### 4. Security

- JWT-based authentication
- Role-based access control (OWNER role required for dashboard)
- Public endpoints for customer booking
- Protected endpoints for staff management

### 5. Database Migrations

- Version-controlled schema changes
- Automatic migration on startup
- Optimistic locking support via version column

---

## Development Notes

### Database Connection Issues

If you encounter "role does not exist" errors:

1. **Reset database completely:**

   ```bash
   cd infra
   docker-compose down -v  # Remove volumes
   docker-compose up -d db
   sleep 15  # Wait for initialization
   ```

2. **Use local profile:**

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

3. **Check database status:**
   ```bash
   docker exec infra-db-1 psql -U oreo -d oreopet -c "SELECT 1;"
   ```

### Testing Race Conditions

The test suite includes tests for concurrent booking scenarios. To manually test:

```bash
# Use Apache Bench or similar tool
ab -n 100 -c 10 -p reservation.json -T application/json \
   http://localhost:8080/api/reservations
```

---

## License

This project is for educational/demonstration purposes.
