package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

public class CategoryAttributes {
  private final long total;
  private final long totalActive;
  private final long totalInactive;

  public CategoryAttributes() {
    this.total = 0L;
    this.totalActive = 0L;
    this.totalInactive = 0L;
  }

  public CategoryAttributes(long total, long totalActive, long totalInactive) {
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
