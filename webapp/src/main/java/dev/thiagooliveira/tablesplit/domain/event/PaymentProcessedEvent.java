package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import java.util.UUID;

public class PaymentProcessedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final UUID orderId;
  private final UUID paymentId;

  public PaymentProcessedEvent(Order order, Payment payment) {
    this.accountId = order.getAccountId();
    this.restaurantId = order.getRestaurantId();
    this.orderId = order.getId();
    this.paymentId = payment.getId();
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getPaymentId() {
    return paymentId;
  }
}
