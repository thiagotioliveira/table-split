package dev.thiagooliveira.tablesplit.application.order.command;

import java.util.List;

public record TicketCommand(String note, List<TicketItemCommand> items) {}
