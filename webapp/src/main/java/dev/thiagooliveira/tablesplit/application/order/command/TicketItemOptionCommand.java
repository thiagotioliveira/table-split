package dev.thiagooliveira.tablesplit.application.order.command;

import java.math.BigDecimal;

public record TicketItemOptionCommand(String text, BigDecimal extraPrice) {}
