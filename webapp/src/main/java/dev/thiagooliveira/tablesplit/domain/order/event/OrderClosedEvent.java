package dev.thiagooliveira.tablesplit.domain.order.event;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderClosedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID orderId;
  private final String tableCod;
  private final BigDecimal totalAmount;
  private final Language language;
  private final UUID initiatedBy;

  public OrderClosedEvent(Order order, String tableCod, Language language, UUID initiatedBy) {
    this.accountId = order.getAccountId();
    this.restaurantId = order.getRestaurantId();
    this.orderId = order.getId();
    this.tableCod = tableCod;
    this.totalAmount = order.calculateTotal();
    this.language = language;
    this.initiatedBy = initiatedBy;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public String getTableCod() {
    return tableCod;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public Language getLanguage() {
    return language;
  }

  public UUID getInitiatedBy() {
    return initiatedBy;
  }
}
