package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

  // -----------------------------------------------------------------------
  // Order
  // -----------------------------------------------------------------------

  public Order toDomain(OrderEntity entity) {
    Order domain = new Order();
    domain.setId(entity.getId());
    domain.setRestaurantId(entity.getRestaurantId());
    domain.setServiceFee(entity.getServiceFee());
    domain.setTableId(entity.getTableId());
    domain.setStatus(entity.getStatus());
    domain.setOpenedAt(entity.getOpenedAt());
    domain.setClosedAt(entity.getClosedAt());
    if (entity.getTickets() != null) {
      domain.setTickets(
          new ArrayList<>(entity.getTickets().stream().map(this::ticketToDomain).toList()));
    }
    if (entity.getPayments() != null) {
      domain.setPayments(
          new ArrayList<>(entity.getPayments().stream().map(this::paymentToDomain).toList()));
    }
    if (entity.getCustomers() != null) {
      domain.setCustomers(
          entity.getCustomers().stream()
              .map(
                  c ->
                      new dev.thiagooliveira.tablesplit.domain.order.OrderCustomer(
                          c.getId(), c.getName()))
              .collect(Collectors.toSet()));
    }
    return domain;
  }

  public OrderEntity toEntity(Order domain) {
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
              domain.getTickets().stream().map(ticket -> ticketToEntity(ticket, entity)).toList()));
    }
    if (domain.getPayments() != null) {
      entity.setPayments(
          new ArrayList<>(
              domain.getPayments().stream()
                  .map(payment -> paymentToEntity(payment, entity))
                  .toList()));
    }
    if (domain.getCustomers() != null) {
      entity.setCustomers(
          domain.getCustomers().stream()
              .map(c -> new OrderCustomerEntity(c.getId(), c.getName()))
              .collect(Collectors.toSet()));
    }
    return entity;
  }

  // -----------------------------------------------------------------------
  // Ticket
  // -----------------------------------------------------------------------

  public Ticket ticketToDomain(TicketEntity entity) {
    Ticket domain = new Ticket();
    domain.setId(entity.getId());
    domain.setStatus(entity.getStatus());
    domain.setCreatedAt(entity.getCreatedAt());
    domain.setReadyAt(entity.getReadyAt());
    domain.setNote(entity.getNote());
    if (entity.getItems() != null) {
      domain.setItems(
          new ArrayList<>(entity.getItems().stream().map(this::ticketItemToDomain).toList()));
    }
    return domain;
  }

  public TicketEntity ticketToEntity(Ticket domain, OrderEntity order) {
    TicketEntity entity = new TicketEntity();
    entity.setId(domain.getId());
    entity.setOrder(order);
    entity.setStatus(domain.getStatus());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setReadyAt(domain.getReadyAt());
    entity.setNote(domain.getNote());
    if (domain.getItems() != null) {
      entity.setItems(
          new ArrayList<>(
              domain.getItems().stream().map(item -> ticketItemToEntity(item, entity)).toList()));
    }
    return entity;
  }

  // -----------------------------------------------------------------------
  // TicketItem
  // -----------------------------------------------------------------------

  public TicketItem ticketItemToDomain(TicketItemEntity entity) {
    TicketItem domain = new TicketItem();
    domain.setId(entity.getId());
    domain.setItemId(entity.getItemId());
    domain.setName(new HashMap<>());
    domain.setCustomerId(entity.getCustomerId());
    domain.setQuantity(entity.getQuantity());
    domain.setUnitPrice(entity.getUnitPrice());
    domain.setNote(entity.getNote());
    domain.setRating(entity.getRating());
    domain.setStatus(entity.getStatus());
    domain.setCustomizations(entity.getCustomizations());
    if (entity.getDiscountType() != null) {
      domain.setPromotionSnapshot(
          new TicketItem.PromotionSnapshot(
              entity.getPromotionId(),
              entity.getOriginalPrice(),
              entity.getDiscountType(),
              entity.getDiscountValue()));
    }
    return domain;
  }

  public TicketItemEntity ticketItemToEntity(TicketItem domain, TicketEntity ticket) {
    TicketItemEntity entity = new TicketItemEntity();
    entity.setId(domain.getId());
    entity.setTicket(ticket);
    entity.setItemId(domain.getItemId());
    entity.setCustomerId(domain.getCustomerId());
    entity.setQuantity(domain.getQuantity());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setNote(domain.getNote());
    entity.setRating(domain.getRating());
    entity.setStatus(domain.getStatus());
    entity.setCustomizations(domain.getCustomizations());
    if (domain.getPromotionSnapshot() != null) {
      var snapshot = domain.getPromotionSnapshot();
      entity.setPromotionId(snapshot.promotionId());
      entity.setOriginalPrice(snapshot.originalPrice());
      entity.setDiscountType(snapshot.discountType());
      entity.setDiscountValue(snapshot.discountValue());
    }
    return entity;
  }

  // -----------------------------------------------------------------------
  // Payment
  // -----------------------------------------------------------------------

  public Payment paymentToDomain(PaymentEntity entity) {
    Payment payment = new Payment();
    payment.setId(entity.getId());
    payment.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
    payment.setCustomerId(entity.getCustomerId());
    payment.setAmount(entity.getAmount());
    payment.setPaidAt(entity.getPaidAt());
    payment.setMethod(entity.getMethod());
    payment.setNote(entity.getNote());
    return payment;
  }

  public PaymentEntity paymentToEntity(Payment domain, OrderEntity order) {
    PaymentEntity entity = new PaymentEntity();
    entity.setId(domain.getId());
    entity.setOrder(order);
    entity.setCustomerId(domain.getCustomerId());
    entity.setAmount(domain.getAmount());
    entity.setPaidAt(domain.getPaidAt());
    entity.setMethod(domain.getMethod());
    entity.setNote(domain.getNote());
    return entity;
  }

  // -----------------------------------------------------------------------
  // OrderFeedback
  // -----------------------------------------------------------------------

  public OrderFeedback feedbackToDomain(OrderFeedbackEntity entity) {
    OrderFeedback domain = new OrderFeedback();
    domain.setId(entity.getId());
    domain.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
    domain.setCustomerId(entity.getCustomerId());
    domain.setRating(entity.getRating());
    domain.setComment(entity.getComment());
    domain.setCreatedAt(entity.getCreatedAt());
    domain.setRead(entity.isRead());
    return domain;
  }

  public OrderFeedbackEntity feedbackToEntity(OrderFeedback domain) {
    OrderFeedbackEntity entity = new OrderFeedbackEntity();
    entity.setId(domain.getId());
    entity.setCustomerId(domain.getCustomerId());
    entity.setRating(domain.getRating());
    entity.setComment(domain.getComment());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setRead(domain.isRead());
    return entity;
  }
}
