package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class CustomerMenuModel {
  private final String profileLink;
  private final RestaurantModel restaurant;
  private final List<CategoryModel> categories = new ArrayList<>();
  private final List<ItemModel> items = new ArrayList<>();
  private final List<TicketItemModel> ticketItems = new ArrayList<>();
  private final List<OrderCustomerModel> customers = new ArrayList<>();
  private final List<PaymentModel> payments = new ArrayList<>();
  private TableModel tableModel;

  public CustomerMenuModel(
      Restaurant restaurant,
      List<Category> categories,
      List<Item> items,
      Table table,
      dev.thiagooliveira.tablesplit.domain.order.Order activeOrder) {
    var symbol = restaurant.getCurrency().getSymbol();
    this.profileLink = String.format("/@%s", restaurant.getSlug());
    this.restaurant = new RestaurantModel(restaurant);
    this.tableModel = table != null ? new TableModel(table) : null;
    categories.forEach(c -> this.categories.add(new CategoryModel(c, items, symbol)));
    items.forEach(i -> this.items.add(new ItemModel(i, symbol)));
    if (activeOrder != null) {
      activeOrder
          .getTickets()
          .forEach(
              t ->
                  t.getItems()
                      .forEach(
                          item ->
                              this.ticketItems.add(
                                  new TicketItemModel(
                                      item,
                                      activeOrder.getCustomerName(item.getCustomerId()),
                                      t.getCreatedAt()))));
      activeOrder.getCustomers().stream().map(OrderCustomerModel::new).forEach(this.customers::add);
      activeOrder.getPayments().stream().map(PaymentModel::new).forEach(this.payments::add);
    }
  }

  public CustomerMenuModel(
      Restaurant restaurant, List<Category> categories, List<Item> items, Table table) {
    this(restaurant, categories, items, table, null);
  }

  public CustomerMenuModel(Restaurant restaurant, List<Category> categories, List<Item> items) {
    this(restaurant, categories, items, null, null);
  }

  public TableModel getTableModel() {
    return tableModel;
  }

  public List<TicketItemModel> getTicketItems() {
    return ticketItems;
  }

  public List<OrderCustomerModel> getCustomers() {
    return customers;
  }

  public boolean hasTable() {
    return this.tableModel != null;
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

  public List<PaymentModel> getPayments() {
    return payments;
  }
}
