package dev.thiagooliveira.tablesplit.application.order.command;

import java.util.List;
import java.util.UUID;

public record PlaceOrderCommand(
    UUID restaurantId,
    String tableCod,
    List<TicketCommand> tickets,
    Integer serviceFee,
    List<CustomerCommand> customers) {}
