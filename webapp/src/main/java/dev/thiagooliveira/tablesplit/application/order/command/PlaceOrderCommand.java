package dev.thiagooliveira.tablesplit.application.order.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.List;
import java.util.UUID;

public record PlaceOrderCommand(
    UUID restaurantId,
    String tableCod,
    List<TicketCommand> tickets,
    Integer serviceFee,
    List<CustomerCommand> customers,
    UUID initiatedBy,
    Language language,
    dev.thiagooliveira.tablesplit.domain.order.PaymentMethod paymentMethod,
    String paymentNote) {}
