package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

public class DefaultDashboardAttributes {
  private UserAttributes user;
  private RestaurantAttributes restaurant;
  private ItemAttributes items = new ItemAttributes();
  private CategoryAttributes categories = new CategoryAttributes();

  public DefaultDashboardAttributes() {}

  public DefaultDashboardAttributes(UserAttributes user) {
    this.user = user;
  }

  public UserAttributes getUser() {
    return user;
  }

  public RestaurantAttributes getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(RestaurantAttributes restaurant) {
    this.restaurant = restaurant;
  }

  public ItemAttributes getItems() {
    return items;
  }

  public void setItems(ItemAttributes items) {
    this.items = items;
  }

  public CategoryAttributes getCategories() {
    return categories;
  }

  public void setCategories(CategoryAttributes categories) {
    this.categories = categories;
  }
}
