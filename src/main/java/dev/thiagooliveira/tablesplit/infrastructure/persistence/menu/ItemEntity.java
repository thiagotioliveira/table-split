package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "items")
public class ItemEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Convert(converter = LanguageMapConverter.class)
  @Column(columnDefinition = "TEXT")
  private Map<Language, String> name = new HashMap<>();

  @Convert(converter = LanguageMapConverter.class)
  @Column(columnDefinition = "TEXT")
  private Map<Language, String> description = new HashMap<>();

  private BigDecimal price;

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemImageEntity> images = new ArrayList<>();

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
    domain.setRestaurantId(this.category.getRestaurantId());
    domain.setCategory(new Category());
    domain.getCategory().setId(this.category.getId());
    domain.getCategory().setName(this.category.getName());
    domain.getCategory().setRestaurantId(this.category.getRestaurantId());
    domain.getCategory().setOrder(this.category.getNumOrder());
    domain.setName(this.name);
    domain.setDescription(this.description);
    domain.setPrice(this.price);
    domain.setImages(new ArrayList<>(this.images.stream().map(ItemImageEntity::toDomain).toList()));
    return domain;
  }

  public static ItemEntity fromDomain(Item domain) {
    var entity = new ItemEntity();
    entity.setId(domain.getId());
    entity.setCategory(new CategoryEntity());
    entity.getCategory().setId(domain.getCategory().getId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setPrice(domain.getPrice());
    if (domain.getImages() != null) {
      entity.setImages(
          new ArrayList<>(domain.getImages().stream().map(ItemImageEntity::fromDomain).toList()));
    }
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

  public List<ItemImageEntity> getImages() {
    return images;
  }

  public void setImages(List<ItemImageEntity> images) {
    this.images = images;
  }
}
