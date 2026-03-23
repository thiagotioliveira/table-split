package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class CustomerMenuModel {
  private final String profileLink;
  private final RestaurantModel restaurant;
  private final List<CategoryModel> categories = new ArrayList<>();
  private final List<ItemModel> items = new ArrayList<>();

  public CustomerMenuModel(Restaurant restaurant, List<Category> categories, List<Item> items) {
    var symbol = restaurant.getCurrency().getSymbol();
    this.profileLink = String.format("/@%s", restaurant.getSlug());
    this.restaurant = new RestaurantModel(restaurant);
    categories.forEach(c -> this.categories.add(new CategoryModel(c, items, symbol)));
    items.forEach(i -> this.items.add(new ItemModel(i, symbol)));
  }

  public String getProfileLink() {
    return profileLink;
  }

  public RestaurantModel getRestaurant() {
    return restaurant;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public List<ItemModel> getItems() {
    return items;
  }
}
