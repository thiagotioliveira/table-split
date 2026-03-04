package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "item_images")
public class ItemImageEntity {

  @Id private UUID id;

  @Column(name = "item_id", nullable = false)
  private UUID itemId;

  private String name;

  private boolean main;

  public static ItemImageEntity fromDomain(ItemImage domain) {
    var entity = new ItemImageEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setMain(domain.isMain());
    entity.setItemId(domain.getItemId());
    return entity;
  }

  public ItemImage toDomain() {
    var domain = new ItemImage();
    domain.setId(this.getId());
    domain.setName(this.getName());
    domain.setItemId(this.getItemId());
    domain.setMain(this.isMain());
    return domain;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ItemImageEntity that = (ItemImageEntity) o;
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

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isMain() {
    return main;
  }

  public void setMain(boolean main) {
    this.main = main;
  }
}
