package dev.thiagooliveira.tablesplit.infrastructure.web.manager.dashboard.model;

public class CategoryModel {
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public CategoryModel(long total, long totalActive, long totalInactive) {
    this.total = total;
    this.totalActive = totalActive;
    this.totalInactive = totalInactive;
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
