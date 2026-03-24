package dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import java.util.UUID;

public class RestaurantImageModel {
  private UUID id;
  private String name;
  private boolean cover;

  public RestaurantImageModel() {}

  public RestaurantImageModel(RestaurantImage domain) {
    this.id = domain.getId();
    this.name = domain.getName();
    this.cover = domain.isCover();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
