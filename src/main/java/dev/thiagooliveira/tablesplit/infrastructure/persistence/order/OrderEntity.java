package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private List<PaymentEntity> payments = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "order_customers", joinColumns = @JoinColumn(name = "order_id"))
  private Set<OrderCustomerEntity> customers = new HashSet<>();

  @Column(nullable = false)
  private ZonedDateTime openedAt;

  private ZonedDateTime closedAt;

  public OrderEntity() {}

  public Order toDomain() {
    Order domain = new Order();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setServiceFee(this.serviceFee);
    domain.setTableId(this.tableId);
    domain.setStatus(this.status);
    domain.setOpenedAt(this.openedAt);
    domain.setClosedAt(this.closedAt);
    if (this.tickets != null) {
      domain.setTickets(
          new ArrayList<>(this.tickets.stream().map(TicketEntity::toDomain).toList()));
    }
    if (this.payments != null) {
      domain.setPayments(
          new ArrayList<>(this.payments.stream().map(PaymentEntity::toDomain).toList()));
    }
    if (this.customers != null) {
      domain.setCustomers(
          this.customers.stream().map(OrderCustomerEntity::toDomain).collect(Collectors.toSet()));
    }
    return domain;
  }

  public static OrderEntity fromDomain(Order domain) {
    OrderEntity entity = new OrderEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setServiceFee(domain.getServiceFee());
    entity.setTableId(domain.getTableId());
    entity.setStatus(domain.getStatus());
    entity.setOpenedAt(domain.getOpenedAt());
    entity.setClosedAt(domain.getClosedAt());
    if (domain.getTickets() != null) {
      entity.setTickets(
          new ArrayList<>(
              domain.getTickets().stream()
                  .map(ticket -> TicketEntity.fromDomain(ticket, entity))
                  .toList()));
    }
    if (domain.getPayments() != null) {
      entity.setPayments(
          new ArrayList<>(domain.getPayments().stream().map(PaymentEntity::fromDomain).toList()));
    }
    if (domain.getCustomers() != null) {
      entity.setCustomers(
          domain.getCustomers().stream()
              .map(OrderCustomerEntity::fromDomain)
              .collect(Collectors.toSet()));
    }
    return entity;
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
}
