package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  public Restaurant toDomain() {
    var domain = new Restaurant();
    domain.setId(this.id);
    domain.setName(this.name);
    return domain;
  }

  public static RestaurantEntity fromDomain(Restaurant restaurant) {
    var entity = new RestaurantEntity();
    entity.setId(restaurant.getId());
    entity.setName(restaurant.getName());
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RestaurantEntity that = (RestaurantEntity) o;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
