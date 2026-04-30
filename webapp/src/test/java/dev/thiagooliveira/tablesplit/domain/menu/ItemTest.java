package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import dev.thiagooliveira.tablesplit.domain.event.ItemCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemUpdatedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemTest {

  @Test
  void shouldRegisterItemCreatedEventOnCreate() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Item item = Item.create(accountId, restaurantId);

    assertNotNull(item.getId());
    assertEquals(accountId, item.getAccountId());
    assertEquals(restaurantId, item.getRestaurantId());
    assertTrue(
        item.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof ItemCreatedEvent
                        && ((ItemCreatedEvent) e).getAccountId().equals(accountId)));
  }

  @Test
  void shouldRegisterItemUpdatedEventOnUpdate() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Item item = Item.create(accountId, restaurantId);
    item.clearEvents();

    item.update();

    assertTrue(
        item.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof ItemUpdatedEvent
                        && ((ItemUpdatedEvent) e).getAccountId().equals(accountId)));
  }

  @Test
  void shouldRegisterItemDeletedEventOnDelete() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Item item = Item.create(accountId, restaurantId);
    item.clearEvents();

    item.delete();

    assertTrue(
        item.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof ItemDeletedEvent
                        && ((ItemDeletedEvent) e).getAccountId().equals(accountId)));
  }
}
