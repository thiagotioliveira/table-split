package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import dev.thiagooliveira.tablesplit.domain.event.CategoryCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryUpdatedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryTest {

  @Test
  void shouldRegisterCategoryCreatedEventOnCreate() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Category category = Category.create(accountId, restaurantId);

    assertNotNull(category.getId());
    assertEquals(accountId, category.getAccountId());
    assertEquals(restaurantId, category.getRestaurantId());
    assertTrue(
        category.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof CategoryCreatedEvent
                        && ((CategoryCreatedEvent) e).getAccountId().equals(accountId)));
  }

  @Test
  void shouldRegisterCategoryUpdatedEventOnUpdate() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Category category = Category.create(accountId, restaurantId);
    category.clearEvents();

    category.update();

    assertTrue(
        category.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof CategoryUpdatedEvent
                        && ((CategoryUpdatedEvent) e).getAccountId().equals(accountId)));
  }

  @Test
  void shouldRegisterCategoryDeletedEventOnDelete() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Category category = Category.create(accountId, restaurantId);
    category.clearEvents();

    category.delete();

    assertTrue(
        category.getDomainEvents().stream()
            .anyMatch(
                e ->
                    e instanceof CategoryDeletedEvent
                        && ((CategoryDeletedEvent) e).getAccountId().equals(accountId)));
  }
}
