package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "items")
public class ItemEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "name_localized_text_id")
  private LocalizedTextEntity name;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "description_localized_text_id")
  private LocalizedTextEntity description;

  private BigDecimal price;

  @Column(nullable = false)
  private boolean active;

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ItemImageEntity> images = new HashSet<>();

  @ElementCollection(targetClass = ItemTag.class)
  @CollectionTable(name = "item_tags", joinColumns = @JoinColumn(name = "item_id"))
  @Column(name = "tag", nullable = false)
  @Enumerated(EnumType.STRING)
  private Set<ItemTag> tags = new HashSet<>();

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
    domain
        .getCategory()
        .setName(
            this.category.getName() != null
                ? this.category.getName().getTranslations()
                : new HashMap<>());
    domain.getCategory().setRestaurantId(this.category.getRestaurantId());
    domain.getCategory().setOrder(this.category.getNumOrder());
    domain.setName(this.name != null ? this.name.getTranslations() : new HashMap<>());
    domain.setDescription(
        this.description != null ? this.description.getTranslations() : new HashMap<>());
    domain.setPrice(this.price);
    domain.setAvailable(this.active);
    domain.setImages(new ArrayList<>(this.images.stream().map(ItemImageEntity::toDomain).toList()));
    domain.setTags(new ArrayList<>(this.tags));
    return domain;
  }

  public static ItemEntity fromDomain(Item domain) {
    var entity = new ItemEntity();
    entity.setId(domain.getId());
    entity.setCategory(new CategoryEntity());
    entity.getCategory().setId(domain.getCategory().getId());
    entity.setName(LocalizedTextEntity.fromMap(domain.getName()));
    entity.setDescription(LocalizedTextEntity.fromMap(domain.getDescription()));
    entity.setPrice(domain.getPrice());
    entity.setActive(domain.isAvailable());
    if (domain.getImages() != null) {
      entity.setImages(
          new HashSet<>(
              domain.getImages().stream()
                  .map(ItemImageEntity::fromDomain)
                  .collect(Collectors.toSet())));
    }
    if (domain.getTags() != null) {
      entity.setTags(new HashSet<>(domain.getTags()));
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

  public LocalizedTextEntity getName() {
    return name;
  }

  public void setName(LocalizedTextEntity name) {
    this.name = name;
  }

  public LocalizedTextEntity getDescription() {
    return description;
  }

  public void setDescription(LocalizedTextEntity description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Set<ItemImageEntity> getImages() {
    return images;
  }

  public void setImages(Set<ItemImageEntity> images) {
    this.images = images;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Set<ItemTag> getTags() {
    return tags;
  }

  public void setTags(Set<ItemTag> tags) {
    this.tags = tags;
  }
}
