package dev.thiagooliveira.tablesplit.domain.order;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Order {
  private UUID id;
  private UUID restaurantId;
  private UUID tableId;
  private int serviceFee;
  private OrderStatus status;
  private List<Ticket> tickets = new ArrayList<>();
  private List<Payment> payments = new ArrayList<>();
  private Set<OrderCustomer> customers = new HashSet<>();
  private ZonedDateTime openedAt;
  private ZonedDateTime closedAt;

  public Order() {}

  public Order(UUID id, UUID restaurantId, UUID tableId, int serviceFee) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableId = tableId;
    this.status = OrderStatus.OPEN;
    this.openedAt = ZonedDateTime.now();
    this.serviceFee = serviceFee;
  }

  public void addPayment(Payment payment) {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.PAYMENT_NOT_ALLOWED);
    }

    BigDecimal remaining = calculateRemainingAmount();
    if (payment.getAmount().compareTo(remaining) > 0) {
      throw new OverpaymentException(this.tableId);
    }

    this.payments.add(payment);
    if (isFullyPaid()) {
      close();
    }
  }

  public void removePayment(UUID paymentId) {
    if (this.status == OrderStatus.CANCELLED) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.PAYMENT_REMOVAL_NOT_ALLOWED);
    }
    this.payments.removeIf(p -> p.getId().equals(paymentId));
    if (this.status == OrderStatus.CLOSED && !isFullyPaid()) {
      this.status = OrderStatus.OPEN;
      this.closedAt = null;
    }
  }

  public BigDecimal calculatePaidAmount() {
    return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal calculateRemainingAmount() {
    return calculateTotal().subtract(calculatePaidAmount());
  }

  public boolean isFullyPaid() {
    return calculateRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
  }

  public void addTicket(Ticket ticket) {
    if (this.status != OrderStatus.OPEN) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.TICKET_NOT_ALLOWED);
    }
    this.tickets.add(ticket);
  }

  public BigDecimal calculateSubtotal() {
    return tickets.stream().map(Ticket::calculateTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal calculateTotal() {
    BigDecimal subtotal = calculateSubtotal();

    BigDecimal fee = feeApplied();

    return subtotal.add(fee);
  }

  public BigDecimal feeApplied() {
    BigDecimal subtotal = calculateSubtotal();

    return subtotal.multiply(BigDecimal.valueOf(serviceFee)).divide(BigDecimal.valueOf(100));
  }

  public void close() {
    if (this.status != OrderStatus.OPEN && this.status != OrderStatus.PENDING) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.CLOSE_NOT_ALLOWED);
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = ZonedDateTime.now();
  }

  public void addCustomer(UUID id, String name) {
    if (id == null) return;
    this.customers.add(new OrderCustomer(id, name));
  }

  public String getCustomerName(UUID id) {
    if (id == null) return "Mesa";
    return customers.stream()
        .filter(c -> c.getId().equals(id))
        .map(OrderCustomer::getName)
        .findFirst()
        .orElse("Desconhecido");
  }

  public Set<OrderCustomer> getCustomers() {
    return customers;
  }

  public void setCustomers(Set<OrderCustomer> customers) {
    this.customers = customers;
  }

  public void updateCustomerName(UUID customerId, String newName) {
    if (customerId == null || newName == null) return;
    customers.stream()
        .filter(c -> c.getId().equals(customerId))
        .findFirst()
        .ifPresent(c -> c.setName(newName));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public UUID getTableId() {
    return tableId;
  }

  public void setTableId(UUID tableId) {
    this.tableId = tableId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public List<Ticket> getTickets() {
    return tickets;
  }

  public void setTickets(List<Ticket> tickets) {
    this.tickets = tickets;
  }

  public List<TicketItem> getItems() {
    return tickets.stream().flatMap(ticket -> ticket.getItems().stream()).toList();
  }

  public ZonedDateTime getOpenedAt() {
    return openedAt;
  }

  public void setOpenedAt(ZonedDateTime openedAt) {
    this.openedAt = openedAt;
  }

  public ZonedDateTime getClosedAt() {
    return closedAt;
  }

  public void setClosedAt(ZonedDateTime closedAt) {
    this.closedAt = closedAt;
  }

  public int getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(int serviceFee) {
    this.serviceFee = serviceFee;
  }

  public List<Payment> getPayments() {
    return payments;
  }

  public void setPayments(List<Payment> payments) {
    this.payments = payments;
  }

  public boolean hasWaitingTickets() {
    return tickets.stream()
        .anyMatch(
            t ->
                t.getStatus() == TicketStatus.PENDING
                    || t.getStatus() == TicketStatus.PREPARING
                    || t.getStatus() == TicketStatus.READY);
  }

  public boolean isInvalid() {
    return tickets.isEmpty()
        || tickets.stream()
            .allMatch(
                t ->
                    t.getItems().stream()
                        .allMatch(item -> item.getStatus() == TicketStatus.CANCELLED));
  }
}
