package dev.thiagooliveira.tablesplit.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TicketTest {

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
