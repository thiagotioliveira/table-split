package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import java.math.BigDecimal;

public class TableSummaryModel {
  private final BigDecimal subtotal;
  private final BigDecimal serviceFee;
  private final BigDecimal total;
  private final BigDecimal paid;
  private final BigDecimal remaining;

  public TableSummaryModel(Order order) {
    if (order != null) {
      this.subtotal = order.calculateSubtotal();
      this.serviceFee = order.feeApplied();
      this.total = order.calculateTotal();
      this.paid = order.calculatePaidAmount();
      this.remaining = order.calculateRemainingAmount();
    } else {
      this.subtotal = BigDecimal.ZERO;
      this.serviceFee = BigDecimal.ZERO;
      this.total = BigDecimal.ZERO;
      this.paid = BigDecimal.ZERO;
      this.remaining = BigDecimal.ZERO;
    }
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public BigDecimal getServiceFee() {
    return serviceFee;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public BigDecimal getPaid() {
    return paid;
  }

  public BigDecimal getRemaining() {
    return remaining;
  }

  // Helper methods for JavaScript to avoid BigDecimal-as-object issues
  public double getSubtotalValue() {
    return subtotal.doubleValue();
  }

  public double getServiceFeeValue() {
    return serviceFee.doubleValue();
  }

  public double getTotalValue() {
    return total.doubleValue();
  }

  public double getPaidValue() {
    return paid.doubleValue();
  }

  public double getRemainingValue() {
    return remaining.doubleValue();
  }
}
