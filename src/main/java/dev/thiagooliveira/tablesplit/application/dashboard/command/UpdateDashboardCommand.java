package dev.thiagooliveira.tablesplit.application.dashboard.command;

public record UpdateDashboardCommand(
    String restaurantName,
    String restaurantAddress,
    String restaurantSlug,
    long totalCategories,
    long totalItems,
    long totalItemsActive,
    long totalItemsInactive) {

  public UpdateDashboardCommand(
      String restaurantName, String restaurantAddress, String restaurantSlug) {
    this(restaurantName, restaurantAddress, restaurantSlug, 0L, 0L, 0L, 0L);
  }
}
