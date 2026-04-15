package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "combo_items")
public class ComboItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "combo_id", nullable = false)
  private UUID comboId;

  @Column(name = "item_id", nullable = false)
  private UUID itemId;

  @Column(nullable = false)
  private int quantity;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getComboId() {
    return comboId;
  }

  public void setComboId(UUID comboId) {
    this.comboId = comboId;
  }

  public UUID getItemId() {
    return itemId;
  }

  public void setItemId(UUID itemId) {
    this.itemId = itemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
