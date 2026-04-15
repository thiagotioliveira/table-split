package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.UUID;

public class ItemImage {
  private UUID id;
  private UUID itemId;
  private String name;
  private boolean main;

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
