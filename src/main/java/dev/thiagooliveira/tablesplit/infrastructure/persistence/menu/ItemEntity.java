package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "items")
public class ItemEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Convert(converter = LanguageMapConverter.class)
  private Map<Language, String> name = new HashMap<>();

  @Convert(converter = LanguageMapConverter.class)
  private Map<Language, String> description = new HashMap<>();

  private BigDecimal price;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ItemEntity that = (ItemEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  public Item toDomain() {
    var domain = new Item();
    domain.setId(this.id);
    domain.setCategoryId(this.category.getId());
    domain.setName(this.name);
    domain.setDescription(this.description);
    domain.setPrice(this.price);
    return domain;
  }

  public static ItemEntity fromDomain(Item domain) {
    var entity = new ItemEntity();
    entity.setId(domain.getId());
    entity.setCategory(new CategoryEntity());
    entity.getCategory().setId(domain.getCategoryId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setPrice(domain.getPrice());
    return entity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public Map<Language, String> getDescription() {
    return description;
  }

  public void setDescription(Map<Language, String> description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }
}
