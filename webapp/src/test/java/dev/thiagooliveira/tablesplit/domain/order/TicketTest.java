package dev.thiagooliveira.tablesplit.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TicketTest {

  @Test
  void shouldUsePromotionalPriceWhenCreatingTicketItem() {
    // Given
    Item item = new Item();
    item.setId(UUID.randomUUID());
    item.setName(Map.of(Language.PT, "Test Item"));
    item.setPrice(new BigDecimal("10.00"));
    item.setPromotion(
        new Item.PromotionInfo(
            UUID.randomUUID(),
            new BigDecimal("8.00"), // promotional price
            DiscountType.FIXED_VALUE,
            new BigDecimal("2.00")));

    UUID customerId = UUID.randomUUID();
    int quantity = 2;
    String note = "Test note";

    // When
    TicketItem ticketItem = new TicketItem(item, quantity, customerId, note);

    // Then
    assertEquals(new BigDecimal("8.00"), ticketItem.getUnitPrice());
    assertEquals(new BigDecimal("16.00"), ticketItem.getTotalPrice());

    // Verify promotion snapshot is captured
    assertNotNull(ticketItem.getPromotionSnapshot());
    assertEquals(
        item.getPromotion().promotionId(), ticketItem.getPromotionSnapshot().promotionId());
    assertEquals(new BigDecimal("10.00"), ticketItem.getPromotionSnapshot().originalPrice());
    assertEquals("FIXED_VALUE", ticketItem.getPromotionSnapshot().discountType());
    assertEquals(new BigDecimal("2.00"), ticketItem.getPromotionSnapshot().discountValue());
  }

  @Test
  void shouldUseRegularPriceWhenNoPromotionExists() {
    // Given
    Item item = new Item();
    item.setId(UUID.randomUUID());
    item.setName(Map.of(Language.PT, "Test Item"));
    item.setPrice(new BigDecimal("10.00"));
    // No promotion set

    UUID customerId = UUID.randomUUID();
    int quantity = 2;
    String note = "Test note";

    // When
    TicketItem ticketItem = new TicketItem(item, quantity, customerId, note);

    // Then
    assertEquals(new BigDecimal("10.00"), ticketItem.getUnitPrice());
    assertEquals(new BigDecimal("20.00"), ticketItem.getTotalPrice());

    // Verify no promotion snapshot is captured
    assertNull(ticketItem.getPromotionSnapshot());
  }

  @Test
  void shouldUpdateStatusToDeliveredWhenAllItemsAreCompletedAndAtLeastOneIsDelivered() {
    TicketItem item1 = createItem(TicketStatus.DELIVERED);
    TicketItem item2 = createItem(TicketStatus.CANCELLED);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PREPARING);
    ticket.setItems(List.of(item1, item2));

    ticket.recalculateStatus();

    assertEquals(TicketStatus.DELIVERED, ticket.getStatus());
  }

  @Test
  void shouldUpdateStatusToCancelledWhenAllItemsAreCancelled() {
    TicketItem item1 = createItem(TicketStatus.CANCELLED);
    TicketItem item2 = createItem(TicketStatus.CANCELLED);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PENDING);
    ticket.setItems(List.of(item1, item2));

    ticket.recalculateStatus();

    assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
  }

  @Test
  void shouldNotUpdateStatusWhenAtLeastOneItemIsStillInProgress() {
    TicketItem item1 = createItem(TicketStatus.DELIVERED);
    TicketItem item2 = createItem(TicketStatus.PREPARING);
    Ticket ticket = new Ticket();
    ticket.setStatus(TicketStatus.PREPARING);
    ticket.setItems(List.of(item1, item2));

    ticket.recalculateStatus();

    assertEquals(TicketStatus.PREPARING, ticket.getStatus());
  }

  private TicketItem createItem(TicketStatus status) {
    TicketItem item = new TicketItem();
    item.setId(UUID.randomUUID());
    item.setStatus(status);
    return item;
  }
}
