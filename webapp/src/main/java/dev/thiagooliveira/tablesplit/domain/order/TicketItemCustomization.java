package dev.thiagooliveira.tablesplit.domain.order;

import java.math.BigDecimal;
import java.util.List;

public record TicketItemCustomization(String title, List<TicketItemOption> options) {
  public BigDecimal calculateTotalExtra() {
    return options.stream()
        .map(TicketItemOption::extraPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
