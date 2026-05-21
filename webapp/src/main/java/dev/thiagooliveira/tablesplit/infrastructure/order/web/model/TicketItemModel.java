package dev.thiagooliveira.tablesplit.infrastructure.order.web.model;

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
  private final String ticketNote;
  private final String status;
  private final String statusClass;
  private final Integer rating;
  private final PromotionInfo promotionSnapshot;
  private final java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
      customizations;
  private final String customizationSummary;
  private final String cancellationReason;
  private final String cancellationReasonLabel;

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
      String ticketNote,
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
        ticketNote,
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
      String ticketNote,
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
        ticketNote,
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
      String ticketNote,
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
        quantity,
        unitPrice,
        totalPrice,
        note,
        ticketNote,
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
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String note,
      String ticketNote,
      String status,
      String statusClass,
      Integer rating,
      ZonedDateTime createdAt,
      PromotionInfo promotionSnapshot,
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations,
      String customizationSummary) {
    this(
        id,
        customerId,
        customerName,
        name,
        quantity,
        unitPrice,
        totalPrice,
        note,
        ticketNote,
        status,
        statusClass,
        rating,
        createdAt,
        promotionSnapshot,
        customizations,
        customizationSummary,
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
      String ticketNote,
      String status,
      String statusClass,
      Integer rating,
      ZonedDateTime createdAt,
      PromotionInfo promotionSnapshot,
      java.util.List<dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization>
          customizations,
      String customizationSummary,
      String cancellationReason,
      String cancellationReasonLabel) {
    this.id = id;
    this.customerId = customerId;
    this.customerName = customerName;
    this.name = name;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.note = note;
    this.ticketNote = ticketNote;
    this.status = status;
    this.statusClass = statusClass;
    this.rating = rating;
    this.createdAt = createdAt;
    this.promotionSnapshot = promotionSnapshot;
    this.customizations = customizations;
    this.customizationSummary = customizationSummary;
    this.cancellationReason = cancellationReason;
    this.cancellationReasonLabel = cancellationReasonLabel;
  }

  public static TicketItemModel fromDomain(
      dev.thiagooliveira.tablesplit.domain.order.TicketItem item,
      String customerName,
      String ticketNote,
      java.time.ZonedDateTime createdAt,
      dev.thiagooliveira.tablesplit.domain.common.Language userLanguage,
      String statusLabel,
      String statusClass) {
    return fromDomain(
        item, customerName, ticketNote, createdAt, userLanguage, statusLabel, statusClass, null);
  }

  public static TicketItemModel fromDomain(
      dev.thiagooliveira.tablesplit.domain.order.TicketItem item,
      String customerName,
      String ticketNote,
      java.time.ZonedDateTime createdAt,
      dev.thiagooliveira.tablesplit.domain.common.Language userLanguage,
      String statusLabel,
      String statusClass,
      String cancellationReasonLabel) {

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

    String cancellationReason =
        item.getCancellationReason() != null ? item.getCancellationReason().name() : null;

    return new TicketItemModel(
        item.getId(),
        item.getCustomerId(),
        customerName,
        itemName,
        item.getQuantity(),
        item.getUnitPrice(),
        item.getTotalPrice(),
        item.getNote(),
        ticketNote,
        statusLabel,
        statusClass,
        item.getRating(),
        createdAt,
        promotionInfo,
        item.getCustomizations(),
        getCustomizationSummary(item.getCustomizations()),
        cancellationReason,
        cancellationReasonLabel);
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

  public String getTicketNote() {
    return ticketNote;
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

  public String getCancellationReason() {
    return cancellationReason;
  }

  public String getCancellationReasonLabel() {
    return cancellationReasonLabel;
  }

  public record PromotionInfo(
      BigDecimal originalPrice,
      BigDecimal promotionalPrice,
      String discountType,
      BigDecimal discountValue) {}
}
