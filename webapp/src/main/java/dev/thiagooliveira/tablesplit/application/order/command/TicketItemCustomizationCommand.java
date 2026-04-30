package dev.thiagooliveira.tablesplit.application.order.command;

import java.util.List;

public record TicketItemCustomizationCommand(String title, List<TicketItemOptionCommand> options) {}
