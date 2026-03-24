package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import java.util.UUID;

public class RestauranteImageModel {
  private UUID id;
  private String name;
  private boolean cover;

  public RestauranteImageModel() {}

  public RestauranteImageModel(RestauranteImage domain) {
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
