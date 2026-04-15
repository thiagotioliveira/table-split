package dev.thiagooliveira.tablesplit.domain.menu;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Combo {
  private UUID id;
  private UUID restaurantId;
  private String name;
  private String description;
  private List<ComboItem> items;
  private BigDecimal comboPrice;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private boolean active;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ComboItem> getItems() {
    return items;
  }

  public void setItems(List<ComboItem> items) {
    this.items = items;
  }

  public BigDecimal getComboPrice() {
    return comboPrice;
  }

  public void setComboPrice(BigDecimal comboPrice) {
    this.comboPrice = comboPrice;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public static class ComboItem {
    private final String itemId;
    private final int quantity;

    public ComboItem(UUID itemId, int quantity) {
      this.itemId = itemId.toString();
      this.quantity = quantity;
    }

    public String getItemId() {
      return itemId;
    }

    public int getQuantity() {
      return quantity;
    }
  }
}
