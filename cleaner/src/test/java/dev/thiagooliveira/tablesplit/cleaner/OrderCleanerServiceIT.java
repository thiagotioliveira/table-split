package dev.thiagooliveira.tablesplit.cleaner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.sql.Connection;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("integration-test")
@Testcontainers
class OrderCleanerServiceIT {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired private OrderCleanerService orderCleanerService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private DataSource dataSource;

  private static final UUID RESTAURANT_ID = UUID.randomUUID();
  private static final String TENANT_SCHEMA =
      "t_" + RESTAURANT_ID.toString().replace("-", "_").toLowerCase();

  private static boolean schemaInitialized = false;

  @BeforeAll
  static void initSchema(
      @Autowired DataSource dataSource,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired Environment environment)
      throws Exception {
    if (schemaInitialized) {
      return;
    }

    // 1. Run Liquibase for public schema
    runLiquibase(dataSource, "public", "db/changelog/db.changelog-master.yaml");

    // 2. Create tenant schema and run Liquibase
    jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + TENANT_SCHEMA);
    runLiquibase(dataSource, TENANT_SCHEMA, "db/changelog/db.changelog-tenant-master.yaml");

    // 3. Seed data
    String zoneId = environment.getProperty("app.time.zone-id", "UTC");
    seedTestData(jdbcTemplate, zoneId);

    schemaInitialized = true;
  }

  private static void runLiquibase(DataSource dataSource, String schema, String changelogPath)
      throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      database.setDefaultSchemaName(schema);
      database.setLiquibaseSchemaName(schema);

      File webappResources = new File("../webapp/src/main/resources");
      Liquibase liquibase =
          new Liquibase(changelogPath, new FileSystemResourceAccessor(webappResources), database);
      liquibase.update("");
    }
  }

  private static void seedTestData(JdbcTemplate jdbcTemplate, String zoneId) {
    ZoneId zone = ZoneId.of(zoneId);

    // --- Public schema data ---
    UUID accountId = UUID.randomUUID();
    jdbcTemplate.update(
        "INSERT INTO public.accounts (id, created_at, plan, status) VALUES (?, now(), 'PROFESSIONAL', 'ACTIVE')",
        accountId);

    jdbcTemplate.update(
        "INSERT INTO public.restaurants (id, account_id, name, slug, email, tags, default_language, customer_languages, currency, average_price, days, hash_primary_color, hash_accent_color) "
            + "VALUES (?, ?, 'Test Rest', 'test-rest', 'test@test.com', '[]', 'pt', '[]', 'EUR', '$', '[]', '#000000', '#FFFFFF')",
        RESTAURANT_ID,
        accountId);

    // --- Tenant schema data ---
    String s = TENANT_SCHEMA;

    // Table
    UUID tableId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.restaurant_tables (id, restaurant_id, cod, status) VALUES (?, ?, 'T1', 'AVAILABLE')",
            s),
        tableId,
        RESTAURANT_ID);

    // Category + Item (needed for ticket_items FK)
    UUID localizedTextId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format("INSERT INTO %s.localized_texts (id) VALUES (?)", s), localizedTextId);

    UUID categoryId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.categories (id, restaurant_id, num_order, active) VALUES (?, ?, 0, true)",
            s),
        categoryId,
        RESTAURANT_ID);

    UUID itemId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.items (id, category_id, price, active) VALUES (?, ?, 10.00, true)", s),
        itemId,
        categoryId);

    // ==========================================
    // OLD ORDER (100 days ago — must be deleted)
    // ==========================================
    UUID oldOrderId = UUID.randomUUID();
    OffsetDateTime oldDate = OffsetDateTime.now(zone).minusDays(100);
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.orders (id, restaurant_id, table_id, status, opened_at, closed_at) VALUES (?, ?, ?, 'CLOSED', ?, ?)",
            s),
        oldOrderId,
        RESTAURANT_ID,
        tableId,
        oldDate.minusHours(2),
        oldDate);

    // Old Order → Ticket
    UUID oldTicketId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.tickets (id, order_id, status, created_at) VALUES (?, ?, 'READY', ?)",
            s),
        oldTicketId,
        oldOrderId,
        oldDate.minusHours(1));

    // Old Order → Ticket Item
    UUID oldTicketItemId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.ticket_items (id, ticket_id, item_id, quantity, unit_price, status, customer_id) VALUES (?, ?, ?, 2, 10.00, 'CONFIRMED', ?)",
            s),
        oldTicketItemId,
        oldTicketId,
        itemId,
        customerId);

    // Old Order → Payment
    UUID oldPaymentId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.payments (id, order_id, customer_id, amount, paid_at, method) VALUES (?, ?, ?, 20.00, ?, 'CASH')",
            s),
        oldPaymentId,
        oldOrderId,
        customerId,
        oldDate);

    // Old Order → Order Customer
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.order_customers (order_id, customer_id, customer_name) VALUES (?, ?, 'John Doe')",
            s),
        oldOrderId,
        customerId);

    // Old Order → Feedback
    UUID oldFeedbackId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.order_feedbacks (id, order_id, customer_id, rating, comment, created_at) VALUES (?, ?, ?, 5, 'Great food!', ?)",
            s),
        oldFeedbackId,
        oldOrderId,
        customerId,
        oldDate);

    // =============================================
    // RECENT ORDER (5 days ago — must be preserved)
    // =============================================
    UUID newOrderId = UUID.randomUUID();
    OffsetDateTime newDate = OffsetDateTime.now(zone).minusDays(5);
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.orders (id, restaurant_id, table_id, status, opened_at, closed_at) VALUES (?, ?, ?, 'CLOSED', ?, ?)",
            s),
        newOrderId,
        RESTAURANT_ID,
        tableId,
        newDate.minusHours(2),
        newDate);

    // Recent Order → Ticket
    UUID newTicketId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.tickets (id, order_id, status, created_at) VALUES (?, ?, 'READY', ?)",
            s),
        newTicketId,
        newOrderId,
        newDate.minusHours(1));

    // Recent Order → Ticket Item
    UUID newCustomerId = UUID.randomUUID();
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.ticket_items (id, ticket_id, item_id, quantity, unit_price, status, customer_id) VALUES (?, ?, ?, 1, 10.00, 'CONFIRMED', ?)",
            s),
        UUID.randomUUID(),
        newTicketId,
        itemId,
        newCustomerId);

    // Recent Order → Payment
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.payments (id, order_id, customer_id, amount, paid_at, method) VALUES (?, ?, ?, 10.00, ?, 'CARD')",
            s),
        UUID.randomUUID(),
        newOrderId,
        newCustomerId,
        newDate);

    // Recent Order → Order Customer
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.order_customers (order_id, customer_id, customer_name) VALUES (?, ?, 'Jane Doe')",
            s),
        newOrderId,
        newCustomerId);

    // Recent Order → Feedback
    jdbcTemplate.update(
        String.format(
            "INSERT INTO %s.order_feedbacks (id, order_id, customer_id, rating, created_at) VALUES (?, ?, ?, 4, ?)",
            s),
        UUID.randomUUID(),
        newOrderId,
        newCustomerId,
        newDate);
  }

  @Test
  void shouldDeleteOldOrdersAndKeepNewOnes() {
    // Act
    orderCleanerService.cleanOldOrders();

    // Assert
    jdbcTemplate.execute("SET search_path TO " + TENANT_SCHEMA);

    // Orders: only the recent one remains
    Integer orderCount = jdbcTemplate.queryForObject("SELECT count(*) FROM orders", Integer.class);
    assertThat(orderCount).isEqualTo(1);

    // Tickets: only the recent order's ticket remains
    Integer ticketCount =
        jdbcTemplate.queryForObject("SELECT count(*) FROM tickets", Integer.class);
    assertThat(ticketCount).isEqualTo(1);

    // Ticket Items: only the recent order's ticket item remains
    Integer ticketItemCount =
        jdbcTemplate.queryForObject("SELECT count(*) FROM ticket_items", Integer.class);
    assertThat(ticketItemCount).isEqualTo(1);

    // Payments: only the recent order's payment remains
    Integer paymentCount =
        jdbcTemplate.queryForObject("SELECT count(*) FROM payments", Integer.class);
    assertThat(paymentCount).isEqualTo(1);

    // Order Customers: only the recent order's customer remains
    Integer customerCount =
        jdbcTemplate.queryForObject("SELECT count(*) FROM order_customers", Integer.class);
    assertThat(customerCount).isEqualTo(1);

    // Feedbacks: only the recent order's feedback remains
    Integer feedbackCount =
        jdbcTemplate.queryForObject("SELECT count(*) FROM order_feedbacks", Integer.class);
    assertThat(feedbackCount).isEqualTo(1);
  }
}
