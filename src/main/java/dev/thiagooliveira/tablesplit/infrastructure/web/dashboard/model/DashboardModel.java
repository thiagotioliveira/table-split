package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;

public class DashboardModel {
  private final UserModel user;
  private final RestaurantModel restaurant;
  private final CategoryModel categories;
  private final ItemModel items;

  public DashboardModel(
      AccountContext accountContext,
      RestaurantModel restaurant,
      CategoryModel categories,
      ItemModel items) {
    this.user = new UserModel(accountContext.getUser());
    this.restaurant = restaurant;
    this.categories = categories;
    this.items = items;
  }

  public UserModel getUser() {
    return user;
  }

  public RestaurantModel getRestaurant() {
    return restaurant;
  }

  public CategoryModel getCategories() {
    return categories;
  }

  public ItemModel getItems() {
    return items;
  }
}
