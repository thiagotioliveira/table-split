package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.order.event.*;
import dev.thiagooliveira.tablesplit.domain.order.event.OrderClosedEvent;
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
    order.registerEvent(new TableOpenedEvent(order, table));
    return order;
  }

  public static Order takeaway(UUID restaurantId, int serviceFee) {
    return new Order(UUID.randomUUID(), restaurantId, null, serviceFee);
  }

  public void addPayment(Payment payment, Language language, UUID initiatedBy) {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      throw new IllegalOrderStatusException(
          this.tableId, IllegalOrderStatusException.Reason.PAYMENT_NOT_ALLOWED);
    }

    BigDecimal remaining = calculateRemainingAmount().setScale(2, java.math.RoundingMode.HALF_UP);
    BigDecimal paymentAmount = payment.getAmount().setScale(2, java.math.RoundingMode.HALF_UP);

    if (paymentAmount.compareTo(remaining) > 0) {
      throw new OverpaymentException(this.tableId);
    }

    this.payments.add(payment);
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
    return calculateRemainingAmount()
            .setScale(2, java.math.RoundingMode.HALF_UP)
            .compareTo(BigDecimal.ZERO)
        <= 0;
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

  public void close(Language language, UUID initiatedBy) {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      return;
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = Time.now();
    this.registerEvent(new OrderClosedEvent(this, null, language, initiatedBy));
  }

  public void close(Table table, Language language, UUID initiatedBy) {
    if (this.status == OrderStatus.CLOSED || this.status == OrderStatus.CANCELLED) {
      return;
    }
    this.status = OrderStatus.CLOSED;
    this.closedAt = Time.now();
    table.release();
    this.registerEvent(new OrderClosedEvent(this, table.getCod(), language, initiatedBy));
  }

  public void addCustomer(UUID id, String name) {
    if (id == null) return;
    this.customers.add(new OrderCustomer(id, name));
  }

  public java.util.Optional<String> getCustomerName(UUID id) {
    if (id == null) return java.util.Optional.empty();
    return customers.stream()
        .filter(c -> c.getId().equals(id))
        .map(OrderCustomer::getName)
        .findFirst();
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

  public void addTicketWithItems(
      List<TicketItem> items, String note, String tableCod, UUID initiatedBy, Language language) {
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
    this.registerEvent(new TicketCreatedEvent(this, ticket, tableCod, initiatedBy, language));
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

    this.registerEvent(new TicketItemStatusChangedEvent(this, ticket, item, newStatus));

    if (ticket.getStatus() != oldTicketStatus) {
      this.registerEvent(new TicketStatusChangedEvent(this, ticket, ticket.getStatus()));
    }
  }

  public void cancelTicketItem(
      UUID ticketId,
      UUID itemId,
      int quantityToCancel,
      java.util.Optional<CancellationReason> reason) {
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
      item.setCancellationReason(reason.orElse(null));
      this.registerEvent(
          new TicketItemStatusChangedEvent(this, ticket, item, TicketStatus.CANCELLED));
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
      cancelledItem.setCancellationReason(reason.orElse(null));

      ticket.getItems().add(cancelledItem);
      this.registerEvent(
          new TicketItemStatusChangedEvent(this, ticket, cancelledItem, TicketStatus.CANCELLED));
    }

    ticket.recalculateStatus();
    if (ticket.getStatus() != oldTicketStatus) {
      this.registerEvent(new TicketStatusChangedEvent(this, ticket, ticket.getStatus()));
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

    if (newStatus == TicketStatus.DELIVERED) {
      ticket.setDeliveredAt(Time.now());
    }

    this.registerEvent(new TicketStatusChangedEvent(this, ticket, newStatus));
  }

  public void processPayment(Payment payment, Language language, UUID initiatedBy) {
    this.addPayment(payment, language, initiatedBy);
    this.registerEvent(new PaymentProcessedEvent(this, payment));
  }

  public boolean hasParticipant(UUID customerId) {
    if (customerId == null) return false;
    return this.customers.stream().anyMatch(c -> c.getId().equals(customerId));
  }

  public void transfer(Table sourceTable, Table targetTable) {
    if (this.tableId == null) {
      throw new IllegalArgumentException("Cannot transfer a takeaway order");
    }
    if (!this.tableId.equals(sourceTable.getId())) {
      throw new IllegalArgumentException("Source table does not match order table");
    }
    if (!targetTable.isAvailable()) {
      throw new IllegalArgumentException("Target table is not available");
    }

    this.tableId = targetTable.getId();

    // Target table assumes the source table status (usually OCCUPIED or WAITING)
    targetTable.setStatus(sourceTable.getStatus());
    sourceTable.release();

    this.registerEvent(new TableTransferredEvent(this, sourceTable, targetTable));
    this.registerEvent(new TableStatusChangedEvent(sourceTable));
    this.registerEvent(new TableStatusChangedEvent(targetTable));
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }
}
