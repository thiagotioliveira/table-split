# TableSplit

> **A modern, multi-tenant restaurant management platform** — from table management and order tracking to customer-facing menus and real-time notifications.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Multi-Tenancy](#multi-tenancy)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [Screens & Flows](#screens--flows)
- [WebSocket & Real-Time](#websocket--real-time)
- [Internationalization](#internationalization)
- [Database Migrations](#database-migrations)

---

## Overview

TableSplit is a full-stack web application built with **Spring Boot** and **Thymeleaf** for restaurants and similar hospitality venues. It provides two main surfaces:

| Surface | Who uses it | Path prefix |
|---|---|---|
| **Manager Panel** | Restaurant staff / waiters | `/tables`, `/menu`, `/reports`, … |
| **Customer Menu** | Guests scanning a QR code | `/@{slug}`, `/@{slug}/menu` |

Each restaurant is a **separate tenant** with its own isolated PostgreSQL schema, provisioned automatically on registration.

---

## Features

### 🍽️ Table Management
- Create, view, open, and **delete** tables (physical or soft-delete based on order history)
- Real-time status updates via **WebSocket (STOMP/SockJS)** — status refreshes automatically across all active sessions when a customer joins
- Table statuses: `AVAILABLE` · `WAITING` · `OCCUPIED`
- Soft-delete with `deletedAt` column — deleted tables with order history are recoverable; `CreateTable` automatically resurrects a soft-deleted table if the code is reused

### 📋 Order Management
- Open, manage, close, and track orders per table
- Support for multiple customers per table (split orders)
- Place orders per customer with item quantities and notes
- Ticket item statuses tracked (e.g. `PENDING`, `READY`)
- Partial and full payments
- Payment methods: `CASH` · `CARD` · `MB_WAY`

### 🧾 Menu Management
- Create categories and items with localized names/descriptions (multi-language)
- Upload item images via **Cloudinary**
- Activate / deactivate items
- Promotional pricing:
  - `PERCENTAGE` or `FIXED_VALUE` discounts
  - Time-window (`startTime` / `endTime`), date range, and day-of-week recurrence rules
  - Apply to `ALL_MENU`, specific `CATEGORY`, or individual `ITEM`
  - Best promotion automatically selected per item (lowest resulting price)
  - Promotion snapshots preserved on tickets even after promotion is deleted

### 👤 Customer-Facing Menu
- Public accessible at `/@{slug}/menu` (no login required)
- Customers join via table code → name registration
- Real-time cart with promotional price display (original price struck through)
- Per-customer and per-table order summaries
- Service fee calculation
- Waiter call button
- Customer name editing with validation (min 3 chars, auto-capitalized first character)

### 📊 Reports & Dashboard
- Sales reports with date range and status filters
- Dashboard summary cards (open tables, today's orders, revenue)

### 🔔 Notifications
- Push notification support (Web Push API, `push_subscriptions`)
- Waiter call events

### 👥 Staff Management
- Role-based staff accounts
- Module-level access control
- Staff login separate from restaurant admin

### 🖼️ Gallery
- Restaurant cover image management via Cloudinary
- Item images upload

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Web | Spring MVC + Thymeleaf |
| Security | Spring Security |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL (production) · H2 (local/dev) |
| Migrations | Liquibase |
| Real-time | WebSocket (STOMP + SockJS) |
| Image Storage | Cloudinary |
| Build | Maven |
| Frontend | Vanilla HTML/CSS/JS (no framework) |
| i18n | Spring MessageSource (`messages.properties`, `messages_pt.properties`) |

---

## Architecture

TableSplit follows a **clean / layered architecture**:

```
┌─────────────────────────────────────────┐
│             Presentation Layer          │
│   Thymeleaf templates + Controllers     │
│   REST API endpoints (notifications)   │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│           Application Layer             │
│   Use Cases (GetItem, PlaceOrder,       │
│   CreateTable, DeleteTable, …)          │
│   Repository interfaces (ports)         │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│            Domain Layer                 │
│   Entities: Table, Order, Item,         │
│   Promotion, Coupon, …                  │
│   Domain events, exceptions             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Infrastructure Layer            │
│   JPA Repositories (adapters)           │
│   Cloudinary image storage              │
│   WebSocket broadcasting                │
│   Tenant schema provisioning            │
│   Spring Security configuration         │
└─────────────────────────────────────────┘
```

Use cases are **plain Java classes** instantiated via Spring `@Configuration` beans — they do not extend Spring components directly, keeping the application layer free of framework coupling.

---

## Multi-Tenancy

TableSplit implements **schema-per-tenant** multi-tenancy on PostgreSQL:

1. When a new restaurant registers, `TenantProvisioningService` creates a dedicated PostgreSQL schema.
2. All tenant-specific tables (menu, orders, tables, tickets, promotions, …) live inside that schema.
3. The `TenantFilter` resolves the current tenant from:
   - The authenticated user's `AccountContext` (staff panel)
   - The URL slug for public customer routes (`/@{slug}/…`)
4. Hibernate's `MultiTenantConnectionProvider` routes each query to the correct schema at the connection level.
5. Liquibase runs **per-tenant migrations** using `db.changelog-tenant-master.yaml`.

The `public` schema holds shared tables: `accounts`, `users`, `restaurants`.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 14+ **or** use the built-in H2 for local development

### Local Development (H2)

```bash
# Clone the repo
git clone https://github.com/thiagotioliveira/table-split.git
cd table-split

# Run with the local profile (uses H2 in-memory DB)
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

The application starts on **http://localhost:8080**.

### Production / PostgreSQL

1. Create a PostgreSQL database and user:
   ```sql
   CREATE DATABASE tablesplit;
   CREATE USER tablesplit_user WITH PASSWORD 'yourpassword';
   GRANT ALL PRIVILEGES ON DATABASE tablesplit TO tablesplit_user;
   ```

2. Set the required environment variables (see [Configuration](#configuration)).

3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```

Liquibase will automatically apply all migrations on startup.

---

## Configuration

The application is configured via environment variables (or `application.properties`):

| Variable | Description | Example                                       |
|---|---|-----------------------------------------------|
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/tablesplit` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `tablesplit_user`                             |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `yourpassword`                                |
| `CLOUDINARY_URL` | Cloudinary connection string | `cloudinary://key:secret@cloud`               |
| `SPRING_PROFILES_ACTIVE` | Active profile | `h2` (H2) or `postgres` PostgreSQL            |

> **Note:** The `h2` Spring profile enables H2 in-memory mode. All data is lost on restart. It is intended for quick development and UI testing only.

---

## Project Structure

```
src/
├── main/
│   ├── java/dev/thiagooliveira/tablesplit/
│   │   ├── application/         # Use cases & repository interfaces
│   │   │   ├── account/
│   │   │   ├── image/
│   │   │   ├── menu/            # GetItem, GetCategory, …
│   │   │   ├── notification/
│   │   │   ├── order/           # CreateTable, DeleteTable, PlaceOrder, …
│   │   │   └── restaurant/
│   │   ├── domain/              # Pure domain models & events
│   │   │   ├── menu/            # Item, Promotion, Coupon, DiscountType, …
│   │   │   ├── order/           # Table, Order, Ticket, Payment, …
│   │   │   └── restaurant/
│   │   └── infrastructure/
│   │       ├── config/          # Spring @Configuration beans
│   │       ├── event/
│   │       ├── listener/
│   │       ├── media/           # Cloudinary image storage
│   │       ├── notification/    # Push notifications
│   │       ├── persistence/     # JPA entities & repository adapters
│   │       ├── security/        # Spring Security setup
│   │       ├── tenant/          # Multi-tenant schema routing & provisioning
│   │       ├── transactional/
│   │       └── web/
│   │           ├── api/         # REST API (notifications)
│   │           ├── customer/    # Customer-facing pages
│   │           └── manager/     # Staff management pages
│   └── resources/
│       ├── db/changelog/        # Liquibase changesets
│       ├── messages.properties  # i18n (English)
│       ├── messages_pt.properties # i18n (Portuguese)
│       └── templates/           # Thymeleaf HTML templates
```

---

## Domain Model

```
Restaurant
 └── has many Tables (restaurant_tables)
      └── Table → has many Orders
           └── Order → has many Tickets
                └── Ticket → has many TicketItems
                     └── TicketItem (snapshot: name, unit_price, promotion info)

Restaurant
 └── has many Categories
      └── Category → has many Items
           └── Item → may have active Promotion (calculated at query time)

Restaurant
 └── has many Promotions
      └── Promotion: discountType (PERCENTAGE|FIXED_VALUE)
                     applyType   (ALL_MENU|CATEGORY|ITEM)
                     schedule    (startDate, endDate, daysOfWeek, startTime, endTime)
```

### Table lifecycle

```
AVAILABLE ──open──► OCCUPIED
          ◄─close── OCCUPIED
AVAILABLE ──wait──► WAITING
                    WAITING ──open──► OCCUPIED
OCCUPIED / WAITING ──soft/hard delete──► (removed or deletedAt set)
```

### Promotion snapshot

When an order is placed, each `TicketItem` stores a **snapshot** of the promotion at that moment (`discountType`, `discountValue`, `promotionId`). If the promotion is later deleted, the snapshot is preserved and correctly restored when viewing historical orders.

---

## Screens & Flows

### Manager Panel

| Route | Description |
|---|---|
| `/` | Public landing page |
| `/login` | Restaurant admin login |
| `/register` | New restaurant registration |
| `/tables` | Waiter dashboard — table grid, order panel, quick actions |
| `/menu` | Menu item & category management |
| `/promotions` | Promotion management |
| `/reports` | Sales reports |
| `/staff` | Staff management |
| `/settings` | Restaurant profile & settings |
| `/gallery` | Cover image management |
| `/dashboard` | Summary dashboard |

### Customer Flow

| Route | Description |
|---|---|
| `/@{slug}` | Restaurant public profile |
| `/@{slug}/menu` | Browse menu (public, no login) |
| `/@{slug}/menu?tableCode=X` | Join a table → name prompt → full interactive menu |
| `/table-entry` | Table code entry page |

---

## WebSocket & Real-Time

TableSplit uses **STOMP over SockJS** for real-time updates.

- **Endpoint:** `/ws` (SockJS fallback)
- **Topic:** `/topic/table/{restaurantId}` — broadcasts table status changes
- When a customer joins a table (`OpenTable` use case), `SyncTableStatus` broadcasts a status-changed event to all subscribed sessions (e.g., the waiter's dashboard).
- The waiter dashboard auto-refreshes the table card without a full page reload.

---

## Internationalization

The application supports multiple languages via Spring's `MessageSource`:

| File | Language |
|---|---|
| `messages.properties` | English (default) |
| `messages_pt.properties` | Portuguese |

Language is resolved from the authenticated user's profile or the browser's `Accept-Language` header. The customer menu respects the restaurant's configured `customerLanguages` list.

---

## Database Migrations

All schema changes are managed by **Liquibase**:

| Changelog | Purpose |
|---|---|
| `db.changelog-master.yaml` | Entry point, includes public + tenant masters |
| `db.changelog-public-master.yaml` | Shared tables: `accounts`, `users`, `restaurants` |
| `db.changelog-tenant-master.yaml` | Per-tenant tables (runs in each restaurant's schema) |
| `changesets/` | Incremental migrations (added over time) |

Migrations run automatically on application startup. New tenant schemas are migrated when a restaurant registers.

### Naming convention for changesets

```
{YYYYMMDD}-{short-description}.yaml
```

Example: `20260411-add-deleted-at-to-restaurant-tables.yaml`

---

## License

This project is for personal/portfolio use. All rights reserved © Thiago Oliveira.