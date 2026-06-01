package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ComboTest {

  @Test
  void shouldGetAndSetFieldsSuccessfully() {
    Combo combo = new Combo();
    UUID id = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    combo.setId(id);
    combo.setRestaurantId(restaurantId);
    combo.setName("Combo 1");
    combo.setDescription("Desc 1");
    combo.setComboPrice(BigDecimal.valueOf(35.50));
    combo.setStartDate(now);
    combo.setEndDate(now.plusDays(1));
    combo.setActive(true);

    Combo.ComboItem item1 = new Combo.ComboItem(itemId, 2);
    Combo.ComboItem item2 = new Combo.ComboItem(itemId.toString(), 3);
    combo.setItems(List.of(item1, item2));

    assertEquals(id, combo.getId());
    assertEquals(restaurantId, combo.getRestaurantId());
    assertEquals("Combo 1", combo.getName());
    assertEquals("Desc 1", combo.getDescription());
    assertEquals(BigDecimal.valueOf(35.50), combo.getComboPrice());
    assertEquals(now, combo.getStartDate());
    assertEquals(now.plusDays(1), combo.getEndDate());
    assertTrue(combo.isActive());

    assertEquals(2, combo.getItems().size());
    assertEquals(itemId.toString(), combo.getItems().get(0).getItemId());
    assertEquals(2, combo.getItems().get(0).getQuantity());
    assertEquals(itemId.toString(), combo.getItems().get(1).getItemId());
    assertEquals(3, combo.getItems().get(1).getQuantity());
  }
}
