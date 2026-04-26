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
  private final Integer rating;
  private final PromotionInfo promotionSnapshot;
  private final String customizations;
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
      String customizations) {
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
        rating,
        createdAt,
        promotionSnapshot,
        customizations,
        formatCustomizations(customizations));
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
      String customizations,
      String customizationSummary) {
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

    String itemName = item.getName() != null ? item.getName().get(userLanguage) : null;

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

  public static String getCustomizationSummary(String json) {
    return formatCustomizations(json);
  }

  private static String formatCustomizations(String json) {
    if (json == null || json.isEmpty()) return null;
    try {
      var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
      var root = mapper.readTree(json);
      if (root.isArray()) {
        java.util.List<String> questions = new java.util.ArrayList<>();
        for (var q : root) {
          String title = q.has("title") ? q.get("title").asText() : "";
          var optionsNode = q.get("options");
          if (optionsNode != null && optionsNode.isArray()) {
            java.util.List<String> options = new java.util.ArrayList<>();
            for (var o : optionsNode) {
              options.add(o.get("text").asText());
            }
            String optionsText = String.join(", ", options);
            questions.add(title.isEmpty() ? optionsText : title + " " + optionsText);
          }
        }
        return String.join(" | ", questions);
      }
    } catch (Exception e) {
      return null;
    }
    return null;
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

  public Integer getRating() {
    return rating;
  }

  public PromotionInfo getPromotionSnapshot() {
    return promotionSnapshot;
  }

  public String getCustomizations() {
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
