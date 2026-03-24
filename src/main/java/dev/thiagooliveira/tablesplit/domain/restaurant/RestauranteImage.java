package dev.thiagooliveira.tablesplit.domain.restaurant;

import java.util.UUID;

public class RestauranteImage {
  private UUID id;
  private UUID restaurantId;
  private String name;
  private boolean cover;

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

  public boolean isCover() {
    return cover;
  }

  public void setCover(boolean cover) {
    this.cover = cover;
  }
}
