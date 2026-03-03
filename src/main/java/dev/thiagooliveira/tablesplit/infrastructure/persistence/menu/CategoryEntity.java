package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "categories")
public class CategoryEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID restaurantId;

  private Integer numOrder;

  @Convert(converter = LanguageMapConverter.class)
  private Map<Language, String> name = new HashMap<>();

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemEntity> items = new ArrayList<>();

  public Category toDomain() {
    var domain = new Category();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setOrder(this.numOrder);
    domain.setName(this.name);
    return domain;
  }

  public static CategoryEntity fromDomain(Category domain) {
    var entity = new CategoryEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setNumOrder(domain.getOrder());
    entity.setName(domain.getName());
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CategoryEntity that = (CategoryEntity) o;
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

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public Integer getNumOrder() {
    return numOrder;
  }

  public void setNumOrder(Integer numOrder) {
    this.numOrder = numOrder;
  }
}
