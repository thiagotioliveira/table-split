package dev.thiagooliveira.tablesplit.application.order.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record TicketItemCommand(
    UUID itemId,
    UUID customerId,
    int quantity,
    String note,
    UUID promotionId,
    BigDecimal originalPrice,
    String discountType,
    BigDecimal discountValue,
    List<TicketItemCustomizationCommand> customizations) {}
