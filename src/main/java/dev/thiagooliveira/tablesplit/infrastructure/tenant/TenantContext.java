package dev.thiagooliveira.tablesplit.infrastructure.tenant;

/** Utility to store the current tenant identifier in a ThreadLocal. */
public class TenantContext {

  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static void setCurrentTenant(String tenant) {
    System.out.println("[TenantContext] Setting tenant context to: " + tenant);
    currentTenant.set(tenant);
  }

  public static String getCurrentTenant() {
    return currentTenant.get();
  }

  public static void clear() {
    currentTenant.remove();
  }
}
