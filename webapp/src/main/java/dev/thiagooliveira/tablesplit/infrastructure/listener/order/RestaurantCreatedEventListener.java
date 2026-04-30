package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantOperationService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantProvisioningService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RestaurantCreatedEventListener {

  private static final Logger logger =
      LoggerFactory.getLogger(RestaurantCreatedEventListener.class);

  private final CreateTable createTable;
  private final TenantProvisioningService provisioningService;
  private final TenantOperationService tenantOperationService;

  public RestaurantCreatedEventListener(
      CreateTable createTable,
      TenantProvisioningService provisioningService,
      TenantOperationService tenantOperationService) {
    this.createTable = createTable;
    this.provisioningService = provisioningService;
    this.tenantOperationService = tenantOperationService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RestaurantCreatedEvent event) {
    UUID restaurantId = event.getRestaurantId();
    String tenantId = TenantContext.generateTenantIdentifier(restaurantId);
    logger.debug("[Listener] Starting on(RestaurantCreatedEvent) for tenant: {}", tenantId);
    String originalTenant = TenantContext.getCurrentTenant();
    TenantContext.setCurrentTenant(tenantId);

    try {
      logger.debug("[Listener] Calling provisionTenant for: {}", restaurantId);
      // 1. Provision the schema and run migrations (Internal JDBC connection-based)
      this.provisioningService.provisionTenant(restaurantId);
      logger.debug("[Listener] provisionTenant completed successfully");

      // 2. Execute operations in a new transaction with the context already set
      logger.debug("[Listener] Entering runInNewTransaction for tables...");
      this.tenantOperationService.runInNewTransaction(
          () -> {
            // Create tables using Hibernate
            if (event.getNumberOfTables() > 0) {
              logger.debug(
                  "[Listener] Creating {} tables for tenant: {}",
                  event.getNumberOfTables(),
                  tenantId);
              for (int i = 0; i < event.getNumberOfTables(); i++) {
                this.createTable.execute(
                    event.getAccountId(), restaurantId, String.format("%02d", i + 1));
              }
            } else {
              logger.debug("[Listener] No tables to create (num=0)");
            }
          });
      logger.debug("[Listener] runInNewTransaction completed successfully");
    } catch (Exception e) {
      logger.error("[Listener] ERROR during on(RestaurantCreatedEvent): {}", e.getMessage(), e);
      throw e;
    } finally {
      if (originalTenant != null) {
        TenantContext.setCurrentTenant(originalTenant);
      } else {
        TenantContext.clear();
      }
    }
  }
}
