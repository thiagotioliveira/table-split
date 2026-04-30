package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility to temporarily store restaurantId -> accountId mapping in a ThreadLocal to avoid
 * deadlocks during cross-transaction lookups.
 */
public class AccountIdContext {

  private static final ThreadLocal<Map<UUID, UUID>> accountIds =
      ThreadLocal.withInitial(HashMap::new);

  public static void setAccountId(UUID restaurantId, UUID accountId) {
    accountIds.get().put(restaurantId, accountId);
  }

  public static UUID getAccountId(UUID restaurantId) {
    return accountIds.get().get(restaurantId);
  }

  public static void clear() {
    accountIds.get().clear();
    accountIds.remove();
  }
}
