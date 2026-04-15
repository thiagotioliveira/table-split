package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;
import java.util.List;

public record HistoryResponse(
    List<TicketModel> orders, int totalOrders, BigDecimal totalRevenue, BigDecimal avgTicket) {}
