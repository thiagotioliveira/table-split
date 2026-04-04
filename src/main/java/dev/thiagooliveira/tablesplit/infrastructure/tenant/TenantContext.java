package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility to store the current tenant identifier in a ThreadLocal. */
public class TenantContext {

  private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);

  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static void setCurrentTenant(String tenant) {
    logger.debug("[TenantContext] Setting tenant context to: {}", tenant);
    currentTenant.set(tenant);
  }

  public static String getCurrentTenant() {
    return currentTenant.get();
  }

  public static void clear() {
    currentTenant.remove();
  }

  public static String generateTenantIdentifier(UUID id) {
    if (id == null) return "PUBLIC";
    return "t_" + id.toString().replace("-", "_").toLowerCase();
  }
}
