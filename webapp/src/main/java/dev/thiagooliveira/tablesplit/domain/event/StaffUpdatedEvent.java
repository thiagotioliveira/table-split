package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import java.util.Set;
import java.util.UUID;

public record StaffUpdatedEvent(
    UUID staffId,
    UUID restaurantId,
    UUID accountId,
    Set<Module> addedModules,
    Set<Module> removedModules)
    implements DomainEvent {
  @Override
  public UUID getAccountId() {
    return accountId;
  }
}
