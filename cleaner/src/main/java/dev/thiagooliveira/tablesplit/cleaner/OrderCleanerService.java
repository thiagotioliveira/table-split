package dev.thiagooliveira.tablesplit.cleaner;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderCleanerService {

  private static final Logger logger = LoggerFactory.getLogger(OrderCleanerService.class);

  private final JdbcTemplate jdbcTemplate;
  private final TransactionTemplate transactionTemplate;

  @Value("${cleaner.retention-days:90}")
  private int retentionDays;

  @Value("${app.time.zone-id:UTC}")
  private String zoneId;

  public OrderCleanerService(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.transactionTemplate = transactionTemplate;
  }

  public void cleanOldOrders() {
    ZonedDateTime zdtThreshold =
        ZonedDateTime.now(java.time.ZoneId.of(zoneId)).minusDays(retentionDays);
    OffsetDateTime threshold = zdtThreshold.toOffsetDateTime();
    logger.info("Starting cleanup of old orders for all tenants. Threshold: {}", threshold);

    List<UUID> restaurantIds =
        jdbcTemplate.queryForList("SELECT id FROM public.restaurants", UUID.class);
    logger.info("Found {} restaurants/tenants to process.", restaurantIds.size());

    int successCount = 0;
    int errorCount = 0;

    for (UUID restaurantId : restaurantIds) {
      String schema = generateTenantIdentifier(restaurantId);
      try {
        transactionTemplate.executeWithoutResult(
            status -> {
              cleanTenant(schema, threshold);
            });
        successCount++;
      } catch (Exception e) {
        errorCount++;
        logger.error("Error cleaning tenant schema: {}. Skipping to next tenant.", schema, e);
      }
    }

    logger.info(
        "Global cleanup process completed. Success: {}, Errors: {}", successCount, errorCount);
  }

  private void cleanTenant(String schema, OffsetDateTime threshold) {
    logger.info("Processing tenant schema: {}", schema);

    jdbcTemplate.execute(String.format("SET search_path TO %s, public", schema));

    List<UUID> orderIds =
        jdbcTemplate.queryForList(
            "SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?",
            UUID.class,
            threshold);

    if (orderIds.isEmpty()) {
      logger.info("No old orders found in schema {}.", schema);
      return;
    }

    logger.info("Found {} orders to remove in schema {}.", orderIds.size(), schema);

    // Cascade delete (manual because of missing DB-level cascades)

    int ticketItemsDeleted =
        jdbcTemplate.update(
            "DELETE FROM ticket_items WHERE ticket_id IN (SELECT id FROM tickets WHERE order_id IN (SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?))",
            threshold);
    logger.debug("[{}] Removed {} ticket items.", schema, ticketItemsDeleted);

    int ticketsDeleted =
        jdbcTemplate.update(
            "DELETE FROM tickets WHERE order_id IN (SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?)",
            threshold);
    logger.debug("[{}] Removed {} tickets.", schema, ticketsDeleted);

    int paymentsDeleted =
        jdbcTemplate.update(
            "DELETE FROM payments WHERE order_id IN (SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?)",
            threshold);
    logger.debug("[{}] Removed {} payments.", schema, paymentsDeleted);

    int customersDeleted =
        jdbcTemplate.update(
            "DELETE FROM order_customers WHERE order_id IN (SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?)",
            threshold);
    logger.debug("[{}] Removed {} order customer records.", schema, customersDeleted);

    int feedbacksDeleted =
        jdbcTemplate.update(
            "DELETE FROM order_feedbacks WHERE order_id IN (SELECT id FROM orders WHERE status = 'CLOSED' AND closed_at < ?)",
            threshold);
    logger.debug("[{}] Removed {} feedbacks.", schema, feedbacksDeleted);

    int ordersDeleted =
        jdbcTemplate.update(
            "DELETE FROM orders WHERE status = 'CLOSED' AND closed_at < ?", threshold);

    logger.info("[{}] Cleanup completed. Total orders removed: {}", schema, ordersDeleted);
  }

  private String generateTenantIdentifier(UUID id) {
    if (id == null) return "public";
    return "t_" + id.toString().replace("-", "_").toLowerCase();
  }
}
