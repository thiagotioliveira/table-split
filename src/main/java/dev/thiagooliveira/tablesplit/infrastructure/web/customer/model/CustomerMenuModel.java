package dev.thiagooliveira.tablesplit.infrastructure.web.customer.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.utils.CurrencyMapper;
import java.util.ArrayList;
import java.util.List;

public class CustomerMenuModel {
  private final String profileLink;
  private final String restaurantName;
  private final List<CategoryModel> categories = new ArrayList<>();
  private final List<ItemModel> items = new ArrayList<>();

  public CustomerMenuModel(Restaurant restaurant, List<Category> categories, List<Item> items) {
    var symbol = CurrencyMapper.symbol(restaurant.getCurrency());
    this.profileLink = String.format("/p/%s", restaurant.getSlug());
    this.restaurantName = restaurant.getName();
    categories.forEach(c -> this.categories.add(new CategoryModel(c, items, symbol)));
    items.forEach(i -> this.items.add(new ItemModel(i, symbol)));
  }

  public String getProfileLink() {
    return profileLink;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public List<ItemModel> getItems() {
    return items;
  }
}
