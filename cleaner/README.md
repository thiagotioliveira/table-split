# TableSplit - Cleaner Module

The `cleaner` module is an independent Spring Boot utility responsible for periodically purging old records from the TableSplit database.

## Architecture

*   **Execution Model:** Designed as a short-lived process (run-and-exit) via a `CommandLineRunner`. It does not contain an embedded web server or a built-in scheduler (`@EnableScheduling`).
*   **Trigger:** It should be triggered periodically by an external scheduler (e.g., OS Cron, Kubernetes CronJob, or a CI/CD pipeline scheduler).
*   **Data Access:** Uses lightweight `JdbcTemplate` instead of JPA for performant and decoupled database access.
*   **Multi-Tenancy:** The service dynamically discovers all restaurant instances, iterates over their dedicated PostgreSQL schemas, and executes cleanup tasks contextually.
*   **Transactional Integrity:** Operations are isolated per tenant using a programmatic `TransactionTemplate`. If a failure occurs while processing a specific restaurant's data, only that transaction rolls back, and the service safely proceeds to process the remaining tenants.
*   **Cascading:** Operations handle manual record deletion cascading down through `ticket_items`, `tickets`, `payments`, `order_customers`, and `order_feedbacks` before deleting the parent `orders`, bypassing the need for database-level `ON DELETE CASCADE` rules.

## Configuration

Configure the application via `application.yaml` properties or environment variables:

| Property | Environment Variable | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `spring.datasource.url` | `DATABASE_URL` | `jdbc:postgresql://localhost:5432/tablesplit` | Database connection URL. |
| `spring.datasource.username` | `DATABASE_USERNAME` | `postgres` | Database user. |
| `spring.datasource.password` | `DATABASE_PASSWORD` | `postgres` | Database password. |
| `cleaner.retention-days` | `CLEANER_RETENTION_DAYS` | `90` | Number of days to keep closed orders before deleting them. |
| `app.time.zone-id` | `APP_TIME_ZONE_ID` | `Europe/Lisbon` | The time zone used to calculate the retention cutoff date. |

## Running the Cleaner

To start the cleaner job independently from the command line:

```bash
cd cleaner
../mvnw spring-boot:run
```

To run the integration tests (requires Docker for Testcontainers):

```bash
../mvnw verify -pl cleaner
```
