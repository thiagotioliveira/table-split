package dev.thiagooliveira.tablesplit.infrastructure.order.listener.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record TicketCreatedMessage(
    UUID ticketId,
    String tableCod,
    String customerName,
    String ticketNote,
    List<Item> items,
    BigDecimal total,
    ZonedDateTime createdAt) {
  public record Item(
      UUID id,
      String name,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String ticketNote) {}
}
