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
  private final java.util.Map<String, String> names;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusClass;
  private final Integer rating;
  private final PromotionInfo promotionSnapshot;
  private final java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
      customizations;
  private final String customizationSummary;

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
        java.util.Map.of("pt", name),
        quantity,
        unitPrice,
        totalPrice,
        note,
        status,
        statusClass,
        null,
        createdAt,
        null,
        null,
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
      Integer rating,
      ZonedDateTime createdAt,
      PromotionInfo promotionSnapshot) {
    this(
        id,
        customerId,
        customerName,
        name,
        java.util.Map.of("pt", name),
        quantity,
        unitPrice,
        totalPrice,
        note,
        status,
        statusClass,
        rating,
        createdAt,
        promotionSnapshot,
        null,
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
      Integer rating,
      ZonedDateTime createdAt,
      PromotionInfo promotionSnapshot,
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations) {
    this(
        id,
        customerId,
        customerName,
        name,
        java.util.Map.of("pt", name),
        quantity,
        unitPrice,
        totalPrice,
        note,
        status,
        statusClass,
        rating,
        createdAt,
        promotionSnapshot,
        customizations,
        getCustomizationSummary(customizations));
  }

  public TicketItemModel(
      UUID id,
      UUID customerId,
      String customerName,
      String name,
      java.util.Map<String, String> names,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String status,
      String statusClass,
      Integer rating,
      ZonedDateTime createdAt,
      PromotionInfo promotionSnapshot,
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations,
      String customizationSummary) {
    this.id = id;
    this.customerId = customerId;
    this.customerName = customerName;
    this.name = name;
    this.names = names;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.note = note;
    this.status = status;
    this.statusClass = statusClass;
    this.rating = rating;
    this.createdAt = createdAt;
    this.promotionSnapshot = promotionSnapshot;
    this.customizations = customizations;
    this.customizationSummary = customizationSummary;
  }

  public static TicketItemModel fromDomain(
      dev.thiagooliveira.tablesplit.domain.order.TicketItem item,
      String customerName,
      java.time.ZonedDateTime createdAt,
      dev.thiagooliveira.tablesplit.domain.common.Language userLanguage) {

    String itemName =
        item.getName() != null
            ? item.getName()
                .getOrDefault(
                    userLanguage,
                    item.getName()
                        .getOrDefault(
                            dev.thiagooliveira.tablesplit.domain.common.Language.PT,
                            item.getName().values().stream().findFirst().orElse("Item")))
            : "Item";

    java.util.Map<String, String> names = new java.util.HashMap<>();
    if (item.getName() != null) {
      item.getName().forEach((k, v) -> names.put(k.name().toLowerCase(), v));
    }

    PromotionInfo promotionInfo = null;
    if (item.getPromotionSnapshot() != null) {
      var snapshot = item.getPromotionSnapshot();
      promotionInfo =
          new PromotionInfo(
              snapshot.originalPrice(),
              item.getUnitPrice(),
              snapshot.discountType(),
              snapshot.discountValue());
    }

    return new TicketItemModel(
        item.getId(),
        item.getCustomerId(),
        customerName,
        itemName,
        names,
        item.getQuantity(),
        item.getUnitPrice(),
        item.getTotalPrice(),
        item.getNote(),
        item.getStatus().getLabel(),
        item.getStatus().getCssClass(),
        item.getRating(),
        createdAt,
        promotionInfo,
        item.getCustomizations(),
        getCustomizationSummary(item.getCustomizations()));
  }

  public static String getCustomizationSummary(
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations) {
    if (customizations == null || customizations.isEmpty()) return null;

    return customizations.stream()
        .map(
            q -> {
              String optionsText =
                  q.options().stream()
                      .map(dev.thiagooliveira.tablesplit.domain.order.TicketItemOption::text)
                      .collect(java.util.stream.Collectors.joining(", "));
              return q.title().isEmpty() ? optionsText : q.title() + ": " + optionsText;
            })
        .collect(java.util.stream.Collectors.joining(" | "));
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

  public java.util.Map<String, String> getNames() {
    return names;
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

  public Integer getRating() {
    return rating;
  }

  public PromotionInfo getPromotionSnapshot() {
    return promotionSnapshot;
  }

  public java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
      getCustomizations() {
    return customizations;
  }

  public String getCustomizationSummary() {
    return customizationSummary;
  }

  public record PromotionInfo(
      BigDecimal originalPrice,
      BigDecimal promotionalPrice,
      String discountType,
      BigDecimal discountValue) {}
}
