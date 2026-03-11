package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.domain.dashboard.v1.CategoryAttributes;

public class CategoryModel {
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public CategoryModel(CategoryAttributes attributes) {
    this.total = attributes.getTotal();
    this.totalActive = attributes.getTotalActive();
    this.totalInactive = attributes.getTotalInactive();
  }

  public long getTotal() {
    return total;
  }

  public long getTotalActive() {
    return totalActive;
  }

  public long getTotalInactive() {
    return totalInactive;
  }
}
