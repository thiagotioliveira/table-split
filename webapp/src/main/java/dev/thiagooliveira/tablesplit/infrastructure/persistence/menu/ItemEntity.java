package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemTag;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

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

  @Column(name = "deleted_at")
  private OffsetDateTime deletedAt;

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ItemImageEntity> images = new HashSet<>();

  @ElementCollection(targetClass = ItemTag.class)
  @CollectionTable(name = "item_tags", joinColumns = @JoinColumn(name = "item_id"))
  @Column(name = "tag", nullable = false)
  @Enumerated(EnumType.STRING)
  private Set<ItemTag> tags = new HashSet<>();

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemQuestionEntity> questions = new ArrayList<>();

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

  public List<ItemQuestionEntity> getQuestions() {
    return questions;
  }

  public void setQuestions(List<ItemQuestionEntity> questions) {
    this.questions = questions;
  }

  public OffsetDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(OffsetDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
