package dev.thiagooliveira.tablesplit.domain.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class OrderItem {
  private UUID id;
  private UUID itemId;
  private Map<Language, String>
      name; // Using Map to match Item domain for now, will map to localized_text_id in entity
  private String customerName;
  private int quantity;
  private BigDecimal unitPrice;
  private String note;
  private OrderItemStatus status = OrderItemStatus.PENDING;

  public OrderItem() {}

  public OrderItem(Item item, int quantity, String customerName, String note) {
    this.id = UUID.randomUUID();
    this.itemId = item.getId();
    this.name = item.getName();
    this.customerName = customerName;
    this.quantity = quantity;
    this.unitPrice = item.getPrice();
    this.note = note;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public OrderItemStatus getStatus() {
    return status;
  }

  public void setStatus(OrderItemStatus status) {
    this.status = status;
  }
}
