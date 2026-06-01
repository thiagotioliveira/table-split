package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import dev.thiagooliveira.tablesplit.domain.account.event.StaffUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaffTest {

  @Test
  void shouldGetAndSetFieldsSuccessfully() {
    Staff staff = new Staff();
    UUID id = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();

    staff.setId(id);
    staff.setRestaurantId(restaurantId);
    staff.setFirstName("Thiago");
    staff.setLastName("Oliveira");
    staff.setEmail("thiago@test.com");
    staff.setPhone("123456789");
    staff.setPassword("pass");
    staff.setLanguage(Language.PT);
    staff.setRole(Role.RESTAURANT_ADMIN);
    staff.setEnabled(true);
    staff.setModules(Set.of(Module.MENU));

    assertEquals(id, staff.getId());
    assertEquals(restaurantId, staff.getRestaurantId());
    assertEquals("Thiago", staff.getFirstName());
    assertEquals("Oliveira", staff.getLastName());
    assertEquals("thiago@test.com", staff.getEmail());
    assertEquals("123456789", staff.getPhone());
    assertEquals("pass", staff.getPassword());
    assertEquals(Language.PT, staff.getLanguage());
    assertEquals(Role.RESTAURANT_ADMIN, staff.getRole());
    assertTrue(staff.isEnabled());
    assertEquals(Set.of(Module.MENU), staff.getModules());

    staff.setAccountId(accountId);
    assertEquals(accountId, staff.getAccountId());
  }

  @Test
  void shouldUpdateModulesAndPublishStaffUpdatedEvent() {
    Staff staff = new Staff();
    staff.setId(UUID.randomUUID());
    staff.setRestaurantId(UUID.randomUUID());
    staff.setModules(Set.of(Module.MENU));
    staff.setPassword("old-pass");

    // Update modules: remove MENU, add ORDERS, SETTINGS
    staff.update(
        "Thiago",
        "Oliveira",
        "new@email.com",
        "987654321",
        true,
        Set.of(Module.ORDERS, Module.SETTINGS),
        "new-pass");

    assertEquals("Thiago", staff.getFirstName());
    assertEquals("new@email.com", staff.getEmail());
    assertEquals("new-pass", staff.getPassword());
    assertEquals(Set.of(Module.ORDERS, Module.SETTINGS), staff.getModules());

    assertEquals(1, staff.getDomainEvents().size());
    StaffUpdatedEvent event = (StaffUpdatedEvent) staff.getDomainEvents().iterator().next();
    assertEquals(staff.getId(), event.getStaffId());
    assertEquals(staff.getRestaurantId(), event.getRestaurantId());
    assertEquals(Set.of(Module.ORDERS, Module.SETTINGS), event.getAddedModules());
    assertEquals(Set.of(Module.MENU), event.getRemovedModules());

    UUID accountId = UUID.randomUUID();
    staff.setAccountId(accountId);
    assertEquals(accountId, event.getAccountId());
  }
}
