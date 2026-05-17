package dev.thiagooliveira.tablesplit.infrastructure.order.persistence;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "orders")
public class OrderEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID restaurantId;

  @Column(nullable = true)
  private UUID tableId;

  @Column(nullable = false)
  private int serviceFee;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<TicketEntity> tickets = new HashSet<>();

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<PaymentEntity> payments = new HashSet<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OrderFeedbackEntity> feedbacks = new HashSet<>();

  @ElementCollection
  @CollectionTable(name = "order_customers", joinColumns = @JoinColumn(name = "order_id"))
  private Set<OrderCustomerEntity> customers = new HashSet<>();

  @Column(nullable = false)
  private ZonedDateTime openedAt;

  private ZonedDateTime closedAt;

  public OrderEntity() {}

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

  public Set<TicketEntity> getTickets() {
    return tickets;
  }

  public void setTickets(Set<TicketEntity> tickets) {
    this.tickets = tickets;
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

  public Set<PaymentEntity> getPayments() {
    return payments;
  }

  public void setPayments(Set<PaymentEntity> payments) {
    this.payments = payments;
  }

  public Set<OrderCustomerEntity> getCustomers() {
    return customers;
  }

  public void setCustomers(Set<OrderCustomerEntity> customers) {
    this.customers = customers;
  }

  public Set<OrderFeedbackEntity> getFeedbacks() {
    return feedbacks;
  }

  public void setFeedbacks(Set<OrderFeedbackEntity> feedbacks) {
    this.feedbacks = feedbacks;
  }

  public String getCustomerName(java.util.UUID id) {
    if (id == null) return "Mesa";
    return customers.stream()
        .filter(c -> c.getId().equals(id))
        .map(OrderCustomerEntity::getName)
        .findFirst()
        .orElse("Desconhecido");
  }
}
