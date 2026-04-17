package dev.thiagooliveira.tablesplit.agent.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record IntegrationOrderDTO(
    UUID ticketId,
    String tableCod,
    String customerName,
    List<Item> items,
    BigDecimal total,
    ZonedDateTime createdAt) {
  public record Item(
      UUID id,
      String name,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note) {}
}
