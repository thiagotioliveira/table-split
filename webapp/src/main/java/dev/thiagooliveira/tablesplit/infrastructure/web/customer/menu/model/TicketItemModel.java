package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TicketItemModel {
  private final String id;
  private final String customerId;
  private final String customerName;
  private final java.util.Map<String, String> name;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal totalPrice;
  private final String note;
  private final String status;
  private final String statusLabel;
  private final String statusClass;
  private final Integer rating;
  private final ZonedDateTime createdAt;

  private final PromotionSnapshotModel promotionSnapshot;
  private final java.util.List<CustomizationModel> customizations;

  public TicketItemModel(TicketItem item, String customerName, ZonedDateTime createdAt) {
    this.id = item.getId().toString();
    this.customerId = item.getCustomerId().toString();
    this.customerName = customerName;
    this.name = convertMap(item.getName());
    this.quantity = item.getQuantity();
    this.unitPrice = item.getUnitPrice();
    this.totalPrice = item.getTotalPrice();
    this.note = item.getNote();
    this.status = item.getStatus().name().toLowerCase();
    this.statusLabel = item.getStatus().getLabel();
    this.statusClass = item.getStatus().getCssClass();
    this.rating = item.getRating();
    this.createdAt = createdAt;
    this.promotionSnapshot =
        item.getPromotionSnapshot() != null
            ? new PromotionSnapshotModel(item.getPromotionSnapshot(), item.getUnitPrice())
            : null;
    this.customizations =
        item.getCustomizations() != null
            ? item.getCustomizations().stream().map(CustomizationModel::new).toList()
            : java.util.List.of();
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public java.util.Map<String, String> getName() {
    return name;
  }

  private static java.util.Map<String, String> convertMap(java.util.Map<Language, String> map) {
    if (map == null) return java.util.Map.of();
    return map.entrySet().stream()
        .collect(
            java.util.stream.Collectors.toMap(
                entry -> entry.getKey().name().toLowerCase(), java.util.Map.Entry::getValue));
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

  public String getStatusLabel() {
    return statusLabel;
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

  public PromotionSnapshotModel getPromotionSnapshot() {
    return promotionSnapshot;
  }

  public java.util.List<CustomizationModel> getCustomizations() {
    return customizations;
  }

  public static class CustomizationModel {
    private final String title;
    private final java.util.List<OptionModel> options;

    public CustomizationModel(
        dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization customization) {
      this.title = customization.title();
      this.options = customization.options().stream().map(OptionModel::new).toList();
    }

    public String getTitle() {
      return title;
    }

    public java.util.List<OptionModel> getOptions() {
      return options;
    }
  }

  public static class OptionModel {
    private final String text;
    private final BigDecimal extraPrice;

    public OptionModel(dev.thiagooliveira.tablesplit.domain.order.TicketItemOption option) {
      this.text = option.text();
      this.extraPrice = option.extraPrice();
    }

    public String getText() {
      return text;
    }

    public BigDecimal getExtraPrice() {
      return extraPrice;
    }
  }

  public static class PromotionSnapshotModel {
    private final String promotionId;
    private final BigDecimal originalPrice;
    private final String discountType;
    private final BigDecimal discountValue;
    private final BigDecimal promotionalPrice;

    public PromotionSnapshotModel(
        TicketItem.PromotionSnapshot snapshot, BigDecimal promotionalPrice) {
      this.promotionId = snapshot.promotionId() != null ? snapshot.promotionId().toString() : null;
      this.originalPrice = snapshot.originalPrice();
      this.discountType = snapshot.discountType();
      this.discountValue = snapshot.discountValue();
      this.promotionalPrice = promotionalPrice;
    }

    @JsonProperty("promotionId")
    public String getPromotionId() {
      return promotionId;
    }

    @JsonProperty("originalPrice")
    public BigDecimal getOriginalPrice() {
      return originalPrice;
    }

    @JsonProperty("discountType")
    public String getDiscountType() {
      return discountType;
    }

    @JsonProperty("discountValue")
    public BigDecimal getDiscountValue() {
      return discountValue;
    }

    @JsonProperty("promotionalPrice")
    public BigDecimal getPromotionalPrice() {
      return promotionalPrice;
    }
  }
}
