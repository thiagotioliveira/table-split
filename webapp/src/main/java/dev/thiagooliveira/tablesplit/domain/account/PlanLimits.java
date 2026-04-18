package dev.thiagooliveira.tablesplit.domain.account;

public record PlanLimits(
    int categories,
    int menuItems,
    int galleryImages,
    int promotions,
    int tables,
    int staff,
    int orderRetentionDays) {
  public static final PlanLimits UNLIMITED = new PlanLimits(-1, -1, -1, -1, -1, -1, 0);

  public boolean isUnlimited(int limit) {
    return limit == -1;
  }
}
