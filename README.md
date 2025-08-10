# OreoPetBath

A modern full-stack app for a pet grooming & boarding shop:

**Customer (PUBLIC)** can browse and book without login

**Staff (OWNER)** can sign in to a protected dashboard to manage reservations

## Overview

- **Public site**: Home, Service, Princing, Gallery, Reservation (no login needed)
- **Owner Dashboard**: Auto-refresh Live Table, Multi-key Sorting (Date → Time → Status), Status Update, “Complete” action, Special Note Display
- **Booking rules**: Time-window Conflict Detection for services like Grooming/Washing; **Boarding never conflicts** with other services

---

## Architecture & Stack

```
React + Vite (Tailwind)  →  Spring Boot 3 (JWT, JPA)  →  PostgreSQL 16
```

- **Frontend**: React 18, Vite 7, Tailwind CSS
- **Backend**: Spring Boot 3.5.4, Java 17, Spring Security (JWT), Spring Data JPA, Flyway
- **Database**: PostgreSQL 16
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
    auth/                     # AuthController, JwtService, User, UserRepo
    config/                   # BookingConfig, DataSeed, ReservationProps, SecurityConfig, WebConfig
    controller/               # ReservationController, PetController, ServiceOfferingController
    dto/                      # ReservationRequest, ReservationDTO, ServiceDTO, ServiceOption
    model/                    # Reservation, Pet, ServiceOffering, ReservationStatus
    repo/                     # ReservationRepo, PetRepo, ServiceOfferingRepo
    service/                  # ReservationService, PetService, ServiceOfferingService
  src/main/resources/
    application.properties    # JWT + DB config via env overrides
    db/migration/
      V1_init.sql
```

---

## Configuration (env → properties)

**JWT**

```properties
# src/main/resources/application.properties
app.jwt.secret=${APP_JWT_SECRET:please-change-this-to-32+-bytes}
app.jwt.ttlMillis=${APP_JWT_TTLMILLIS:43200000} # defualt 12 hours
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
  “Complete” → `PATCH /api/reservations/{id}/complete`, then re-fetch list
- **Notes**: Truncated text opens a **scrollable modal**; close button pinned and always visible
- **Auto-refresh**: periodic re-fetch + re-fetch on state-changing actions

---

## Backend

### Security (summary)

- **Public** endpoints back the public pages
- **Owner** endpoints require `Authorization: Bearer <JWT>` with role `OWNER`
- `POST /api/auth/login` is public and returns `{token, username, role}`

> Your `SecurityConfig` should explicitly `permitAll()` for the public paths/APIs used by `Reservation.jsx`, and require `hasRole("OWNER")` for the dashboard APIs used by `OwnerDashboard.jsx`

### Reservation rules

- **Conflict window**: for requested time **T**, check `[T - buffer, T + buffer]` where `buffer = ReservationProps.bufferMinutes()`
- **Boarding exemption**: **boarding never conflicts** with other services and isn’t blocked by them

  - Implemented via a derived query that ignores reservations whose `service.code == "BOARDING"` when checking for clashes

### REST API (Main Ones Used)

**Auth**

`POST /api/auth/login`

**Main Reservations (public + owner)**

- **Create (public)**
  `POST /api/reservations`

- **Cancel (public)**
  `POST /api/reservations/{id}/cancel`

- **Dashboard list (owner)**
  `GET /api/reservations/dashboard?phone=&date=&ts=<ms>`

- **Complete (owner)**
  `PATCH /api/reservations/{id}/complete`

## Minimal “How to Run” (dev)

```bash
# Compose (build + up)
docker compose -f infra/docker-compose.yml up --build

# Frontend: http://localhost:5173
# Backend : http://localhost:8080
```
