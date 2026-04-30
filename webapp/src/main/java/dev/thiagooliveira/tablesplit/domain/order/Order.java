package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Order extends AggregateRoot {
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
  // Transient accountId used for event publishing
  private transient UUID accountId;

  public Order() {}

  public Order(UUID id, UUID restaurantId, UUID tableId, int serviceFee) {
    this.id = id;
    this.restaurantId = restaurantId;
    this.tableId = tableId;
    this.status = OrderStatus.OPEN;
    this.openedAt = Time.now();
    this.serviceFee = serviceFee;
  }

  public static Order open(Table table, int serviceFee) {
    Order order = new Order(UUID.randomUUID(), table.getRestaurantId(), table.getId(), serviceFee);
    order.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TableOpenedEvent(order, table));
    return order;
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

    return subtotal
        .multiply(BigDecimal.valueOf(serviceFee))
        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
  }

  public void close() {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      return;
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = Time.now();
  }

  public void close(Table table) {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      return;
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = Time.now();
    table.release();
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TableClosedEvent(this, table));
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

  public BigDecimal calculateSubtotalByCustomer(UUID customerId) {
    if (customerId == null) {
      // Items not assigned to any specific customer (table level)
      return tickets.stream()
          .flatMap(t -> t.getItems().stream())
          .filter(
              item -> item.getCustomerId() == null && item.getStatus() != TicketStatus.CANCELLED)
          .map(TicketItem::getTotalPrice)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    return tickets.stream()
        .flatMap(t -> t.getItems().stream())
        .filter(
            item ->
                customerId.equals(item.getCustomerId())
                    && item.getStatus() != TicketStatus.CANCELLED)
        .map(TicketItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
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

  public void addTicketWithItems(List<TicketItem> items, String note, String tableCod) {
    if (this.status != OrderStatus.OPEN) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.TICKET_NOT_ALLOWED);
    }

    Ticket ticket = new Ticket();
    ticket.setNote(note);
    items.forEach(
        item -> {
          // Business Rule: Ensure all items have a customer.
          // If not assigned, assign to the first customer or a default one.
          if (item.getCustomerId() == null) {
            if (this.customers.isEmpty()) {
              this.addCustomer(UUID.randomUUID(), "Atendimento Mesa " + this.tableId);
            }
            item.setCustomerId(this.customers.iterator().next().getId());
          }
          ticket.getItems().add(item);
        });

    this.tickets.add(ticket);

    // Register Domain Event
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent(this, ticket, tableCod));
  }

  public void updateTicketItemStatus(UUID ticketId, UUID itemId, TicketStatus newStatus) {
    Ticket ticket =
        this.tickets.stream()
            .filter(t -> t.getId().equals(ticketId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    TicketItem item =
        ticket.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    TicketStatus oldTicketStatus = ticket.getStatus();
    item.setStatus(newStatus);
    ticket.recalculateStatus();

    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent(
            this, ticket, item, newStatus));

    if (ticket.getStatus() != oldTicketStatus) {
      this.registerEvent(
          new dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent(
              this, ticket, ticket.getStatus()));
    }
  }

  public void cancelTicketItem(UUID ticketId, UUID itemId, int quantityToCancel, String reason) {
    Ticket ticket =
        this.tickets.stream()
            .filter(t -> t.getId().equals(ticketId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    TicketItem item =
        ticket.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    TicketStatus oldTicketStatus = ticket.getStatus();

    if (quantityToCancel >= item.getQuantity()) {
      item.setStatus(TicketStatus.CANCELLED);
      if (reason != null && !reason.isBlank()) {
        String note = item.getNote() != null ? item.getNote() : "";
        item.setNote(note + " (Cancelado: " + reason + ")");
      }
      this.registerEvent(
          new dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent(
              this, ticket, item, TicketStatus.CANCELLED));
    } else {
      // Partial cancellation
      int remainingQty = item.getQuantity() - quantityToCancel;
      item.setQuantity(remainingQty);

      TicketItem cancelledItem = new TicketItem();
      cancelledItem.setId(UUID.randomUUID());
      cancelledItem.setItemId(item.getItemId());
      cancelledItem.setName(item.getName());
      cancelledItem.setCustomerId(item.getCustomerId());
      cancelledItem.setQuantity(quantityToCancel);
      cancelledItem.setUnitPrice(item.getUnitPrice());
      cancelledItem.setStatus(TicketStatus.CANCELLED);

      ticket.getItems().add(cancelledItem);
      this.registerEvent(
          new dev.thiagooliveira.tablesplit.domain.event.TicketItemStatusChangedEvent(
              this, ticket, cancelledItem, TicketStatus.CANCELLED));
    }

    ticket.recalculateStatus();
    if (ticket.getStatus() != oldTicketStatus) {
      this.registerEvent(
          new dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent(
              this, ticket, ticket.getStatus()));
    }
  }

  public void moveTicket(UUID ticketId, TicketStatus newStatus) {
    Ticket ticket =
        this.tickets.stream()
            .filter(t -> t.getId().equals(ticketId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

    ticket.setStatus(newStatus);
    ticket
        .getItems()
        .forEach(
            item -> {
              if (item.getStatus() != TicketStatus.CANCELLED) {
                item.setStatus(newStatus);
              }
            });

    if (newStatus == TicketStatus.READY) {
      ticket.setReadyAt(Time.now());
    }

    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.TicketStatusChangedEvent(
            this, ticket, newStatus));
  }

  public void processPayment(Payment payment) {
    this.addPayment(payment);
    this.registerEvent(
        new dev.thiagooliveira.tablesplit.domain.event.PaymentProcessedEvent(this, payment));
  }

  public boolean hasParticipant(UUID customerId) {
    if (customerId == null) return false;
    return this.customers.stream().anyMatch(c -> c.getId().equals(customerId));
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }
}
