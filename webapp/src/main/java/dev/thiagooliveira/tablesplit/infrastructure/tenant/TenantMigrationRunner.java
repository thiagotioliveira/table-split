package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runner that ensures all tenant schemas are up-to-date with the latest Liquibase changes during
 * application startup.
 */
@Component
public class TenantMigrationRunner implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(TenantMigrationRunner.class);

  private final RestaurantJpaRepository restaurantRepository;
  private final TenantProvisioningService provisioningService;

  public TenantMigrationRunner(
      RestaurantJpaRepository restaurantRepository, TenantProvisioningService provisioningService) {
    this.restaurantRepository = restaurantRepository;
    this.provisioningService = provisioningService;
  }

  @Override
  public void run(ApplicationArguments args) {
    logger.debug("[TenantMigrationRunner] Starting migration for all existing tenants...");

    List<RestaurantEntity> restaurants = restaurantRepository.findAll();
    logger.debug("[TenantMigrationRunner] Found {} restaurants to migrate.", restaurants.size());

    for (RestaurantEntity restaurant : restaurants) {
      try {
        logger.debug(
            "[TenantMigrationRunner] Migrating tenant for restaurant: {}", restaurant.getId());
        provisioningService.provisionTenant(restaurant.getId());
      } catch (Exception e) {
        logger.error(
            "[TenantMigrationRunner] Failed to migrate tenant for restaurant {}: {}",
            restaurant.getId(),
            e.getMessage(),
            e);
      }
    }

    logger.debug("[TenantMigrationRunner] Finished migration for all tenants.");
  }
}
