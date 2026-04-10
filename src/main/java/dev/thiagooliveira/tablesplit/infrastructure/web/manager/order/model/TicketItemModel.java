package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class TicketItemModel {
  private final UUID id;
  private final UUID customerId;
  private final String customerName;
  private final String name;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusClass;
  private final PromotionInfo promotion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private final ZonedDateTime createdAt;

  public TicketItemModel(
      UUID id,
      UUID customerId,
      String customerName,
      String name,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String status,
      String statusClass,
      ZonedDateTime createdAt) {
    this(
        id,
        customerId,
        customerName,
        name,
        quantity,
        unitPrice,
        totalPrice,
        note,
        status,
        statusClass,
        createdAt,
        null);
  }

  public TicketItemModel(
      UUID id,
      UUID customerId,
      String customerName,
      String name,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String status,
      String statusClass,
      ZonedDateTime createdAt,
      PromotionInfo promotion) {
    this.id = id;
    this.customerId = customerId;
    this.customerName = customerName;
    this.name = name;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.note = note;
    this.status = status;
    this.statusClass = statusClass;
    this.createdAt = createdAt;
    this.promotion = promotion;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public String getNote() {
    return note;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusClass() {
    return statusClass;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public PromotionInfo getPromotion() {
    return promotion;
  }

  public record PromotionInfo(
      BigDecimal originalPrice,
      BigDecimal promotionalPrice,
      String discountType,
      BigDecimal discountValue) {}
}
