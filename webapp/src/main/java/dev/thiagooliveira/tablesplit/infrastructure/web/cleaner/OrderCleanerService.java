package dev.thiagooliveira.tablesplit.infrastructure.web.cleaner;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.order.OrderEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.order.OrderJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderCleanerService {

  private static final Logger logger = LoggerFactory.getLogger(OrderCleanerService.class);

  private final RestaurantJpaRepository restaurantJpaRepository;
  private final OrderJpaRepository orderJpaRepository;
  private final TransactionTemplate transactionTemplate;

  @PersistenceContext private EntityManager entityManager;

  @Value("${app.cleaner.retention-days}")
  private int retentionDays;

  @Value("${app.time.zone-id}")
  private String zoneId;

  public OrderCleanerService(
      RestaurantJpaRepository restaurantJpaRepository,
      OrderJpaRepository orderJpaRepository,
      TransactionTemplate transactionTemplate) {
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.orderJpaRepository = orderJpaRepository;
    this.transactionTemplate = transactionTemplate;
  }

  public void cleanOldOrders() {
    ZonedDateTime threshold = ZonedDateTime.now(ZoneId.of(zoneId)).minusDays(retentionDays);
    logger.info(
        "Starting cleanup of old orders for all tenants. Retention Days: {} Threshold: {}",
        retentionDays,
        threshold);

    List<RestaurantEntity> restaurants = restaurantJpaRepository.findAll();
    logger.info("Found {} restaurants/tenants to process.", restaurants.size());

    int successCount = 0;
    int errorCount = 0;

    for (RestaurantEntity restaurant : restaurants) {
      try {
        cleanTenant(restaurant, threshold);
        successCount++;
      } catch (Exception e) {
        errorCount++;
        logger.error(
            "Error cleaning tenant for restaurant: {}. Skipping to next tenant.",
            restaurant.getId(),
            e);
      }
    }

    logger.info(
        "Global cleanup process completed. Success: {}, Errors: {}", successCount, errorCount);
  }

  public void cleanTenant(RestaurantEntity restaurant, ZonedDateTime threshold) {
    String schema = TenantContext.generateTenantIdentifier(restaurant.getId());
    TenantContext.setCurrentTenant(schema);

    try {
      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      transactionTemplate.executeWithoutResult(
          status -> {
            var slug = restaurant.getSlug();
            logger.info(
                "Processing tenant '{}' schema: {} with threshold: {}", slug, schema, threshold);

            // Manual fallback to ensure search_path is correct for this specific connection
            // We detect the DB and use the correct syntax for H2 or PostgreSQL
            Session session = entityManager.unwrap(Session.class);
            session.doWork(
                connection -> {
                  String dbName = connection.getMetaData().getDatabaseProductName();
                  String sql;
                  if ("H2".equalsIgnoreCase(dbName)) {
                    sql = String.format("SET SCHEMA_SEARCH_PATH %s, public", schema);
                  } else {
                    sql = String.format("SET search_path TO %s, public", schema);
                  }
                  connection.createStatement().execute(sql);
                });

            List<OrderEntity> ordersToRemove =
                orderJpaRepository.findAllByStatusAndClosedAtBefore(OrderStatus.CLOSED, threshold);

            if (ordersToRemove.isEmpty()) {
              logger.info("No old orders found in '{}' schema {}.", slug, schema);
              return;
            }

            logger.info(
                "Found {} orders to remove in '{}' schema {}.",
                ordersToRemove.size(),
                slug,
                schema);

            // Delete orders (this will cascade to tickets, items, payments, feedbacks, etc.)
            orderJpaRepository.deleteAll(ordersToRemove);

            logger.info(
                "'{}' [{}] Cleanup completed. Total orders removed: {}",
                slug,
                schema,
                ordersToRemove.size());
          });
    } finally {
      TenantContext.clear();
    }
  }
}
