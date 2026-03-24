package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestauranteImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurant_images")
public class RestauranteImageEntity {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private boolean cover;

  public static RestauranteImageEntity fromDomain(RestauranteImage domain) {
    var entity = new RestauranteImageEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setName(domain.getName());
    entity.setCover(domain.isCover());
    return entity;
  }

  public RestauranteImage toDomain() {
    var domain = new RestauranteImage();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setName(this.name);
    domain.setCover(this.cover);
    return domain;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RestauranteImageEntity that = (RestauranteImageEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
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

  public boolean isCover() {
    return cover;
  }

  public void setCover(boolean cover) {
    this.cover = cover;
  }
}
