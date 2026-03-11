package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model;

import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;

public class DashboardModel {
  private final UserModel user;
  private final RestaurantModel restaurant;
  private final CategoryModel categories;
  private final ItemModel items;

  public DashboardModel(DefaultDashboardAttributes attributes) {
    this.user = new UserModel(attributes.getUser());
    this.restaurant = new RestaurantModel(attributes.getRestaurant());
    this.categories = new CategoryModel(attributes.getCategories());
    this.items = new ItemModel(attributes.getItems());
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
