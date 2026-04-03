package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantOperationService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantProvisioningService;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RestaurantCreatedEventListener {

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
    String tenantId = "T_" + restaurantId.toString().replace("-", "_").toUpperCase();

    String originalTenant = TenantContext.getCurrentTenant();
    TenantContext.setCurrentTenant(tenantId);

    try {
      // 2. Execute operations in a new transaction with the context already set
      this.tenantOperationService.runInNewTransaction(
          () -> {
            // 1. Provision the schema and run migrations (Internal connection-based)
            this.provisioningService.provisionTenant(restaurantId);

            // 2. Create tables using Hibernate
            if (event.getNumberOfTables() > 0) {
              System.out.println(
                  "[Listener] Creating "
                      + event.getNumberOfTables()
                      + " tables for tenant: "
                      + tenantId);
              for (int i = 0; i < event.getNumberOfTables(); i++) {
                this.createTable.execute(restaurantId, String.format("%02d", i + 1));
              }
            }
          });
    } finally {
      if (originalTenant != null) {
        TenantContext.setCurrentTenant(originalTenant);
      } else {
        TenantContext.clear();
      }
    }
  }
}
