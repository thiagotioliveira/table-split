package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "orders")
public class OrderEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private UUID tableId;

  @Column(nullable = false)
  private int serviceFee;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TicketEntity> tickets = new ArrayList<>();

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<PaymentEntity> payments = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderFeedbackEntity> feedbacks = new ArrayList<>();

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

  public List<TicketEntity> getTickets() {
    return tickets;
  }

  public void setTickets(List<TicketEntity> tickets) {
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

  public List<PaymentEntity> getPayments() {
    return payments;
  }

  public void setPayments(List<PaymentEntity> payments) {
    this.payments = payments;
  }

  public Set<OrderCustomerEntity> getCustomers() {
    return customers;
  }

  public void setCustomers(Set<OrderCustomerEntity> customers) {
    this.customers = customers;
  }

  public List<OrderFeedbackEntity> getFeedbacks() {
    return feedbacks;
  }

  public void setFeedbacks(List<OrderFeedbackEntity> feedbacks) {
    this.feedbacks = feedbacks;
  }
}
