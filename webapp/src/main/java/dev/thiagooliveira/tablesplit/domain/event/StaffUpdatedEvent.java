package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import java.util.Set;
import java.util.UUID;

public class StaffUpdatedEvent implements DomainEvent {
  private final UUID staffId;
  private final UUID restaurantId;
  private UUID accountId;
  private final Set<Module> addedModules;
  private final Set<Module> removedModules;

  public StaffUpdatedEvent(
      UUID staffId,
      UUID restaurantId,
      UUID accountId,
      Set<Module> addedModules,
      Set<Module> removedModules) {
    this.staffId = staffId;
    this.restaurantId = restaurantId;
    this.accountId = accountId;
    this.addedModules = addedModules;
    this.removedModules = removedModules;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getStaffId() {
    return staffId;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public Set<Module> getAddedModules() {
    return addedModules;
  }

  public Set<Module> getRemovedModules() {
    return removedModules;
  }
}
