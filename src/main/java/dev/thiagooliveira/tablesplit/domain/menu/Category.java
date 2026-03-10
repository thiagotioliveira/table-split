package dev.thiagooliveira.tablesplit.domain.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Map;
import java.util.UUID;

public class Category {
  private UUID id;
  private UUID restaurantId;
  private Integer order;
  private Map<Language, String> name;

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

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }
}
