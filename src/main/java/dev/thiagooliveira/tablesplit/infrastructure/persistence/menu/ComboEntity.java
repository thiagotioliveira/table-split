package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "combos")
public class ComboEntity {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(name = "combo_price", nullable = false)
  private BigDecimal comboPrice;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(nullable = false)
  private boolean active;

  @OneToMany(mappedBy = "comboId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ComboItemEntity> items = new ArrayList<>();

  public Combo toDomain() {
    var domain = new Combo();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setName(this.name);
    domain.setDescription(this.description);
    domain.setComboPrice(this.comboPrice);
    domain.setStartDate(this.startDate);
    domain.setEndDate(this.endDate);
    domain.setActive(this.active);
    domain.setItems(
        this.items.stream()
            .map(i -> new Combo.ComboItem(i.getItemId(), i.getQuantity()))
            .collect(Collectors.toList()));
    return domain;
  }

  public static ComboEntity fromDomain(Combo domain) {
    var entity = new ComboEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setComboPrice(domain.getComboPrice());
    entity.setStartDate(domain.getStartDate());
    entity.setEndDate(domain.getEndDate());
    entity.setActive(domain.isActive());
    if (domain.getItems() != null) {
      entity.setItems(
          domain.getItems().stream()
              .map(
                  i -> {
                    var itemEntity = new ComboItemEntity();
                    itemEntity.setComboId(domain.getId());
                    itemEntity.setItemId(i.itemId());
                    itemEntity.setQuantity(i.quantity());
                    return itemEntity;
                  })
              .collect(Collectors.toList()));
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

  public List<ComboItemEntity> getItems() {
    return items;
  }

  public void setItems(List<ComboItemEntity> items) {
    this.items = items;
  }
}
