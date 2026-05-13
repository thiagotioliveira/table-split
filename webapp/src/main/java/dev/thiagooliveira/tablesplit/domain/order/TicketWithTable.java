package dev.thiagooliveira.tablesplit.domain.order;

public record TicketWithTable(Ticket ticket, Order order, String tableCod) {}
