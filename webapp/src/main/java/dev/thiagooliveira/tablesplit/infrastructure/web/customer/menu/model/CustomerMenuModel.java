package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMenuModel {
  private final String profileLink;
  private final RestaurantModel restaurant;
  private final List<CategoryModel> categories = new ArrayList<>();
  private final List<ItemModel> items = new ArrayList<>();
  private final List<TicketItemModel> ticketItems = new ArrayList<>();
  private final List<OrderCustomerModel> customers = new ArrayList<>();
  private final List<PaymentModel> payments = new ArrayList<>();
  private TableModel tableModel;
  private final java.util.UUID orderId;
  private final boolean reviewMode;
  private boolean hasSentFeedback;
  private TableSummaryModel tableSummary;

  public CustomerMenuModel(
      Restaurant restaurant,
      List<Category> categories,
      List<Item> items,
      Table table,
      dev.thiagooliveira.tablesplit.domain.order.Order activeOrder,
      java.time.ZoneId zoneId,
      org.springframework.context.MessageSource messageSource) {
    var symbol = restaurant.getCurrency().getSymbol();
    this.profileLink = String.format("/@%s", restaurant.getSlug());
    this.restaurant = new RestaurantModel(restaurant, zoneId, messageSource);
    this.tableModel = table != null ? new TableModel(table) : null;
    this.orderId = activeOrder != null ? activeOrder.getId() : null;
    this.reviewMode =
        activeOrder != null
            && activeOrder.getStatus()
                != dev.thiagooliveira.tablesplit.domain.order.OrderStatus.OPEN;
    this.tableSummary = new TableSummaryModel(activeOrder);
    categories.forEach(c -> this.categories.add(new CategoryModel(c, items, symbol)));
    items.forEach(i -> this.items.add(new ItemModel(i, symbol)));
    if (activeOrder != null) {
      activeOrder
          .getTickets()
          .forEach(
              t ->
                  t.getItems()
                      .forEach(
                          item -> {
                            var ticketItem =
                                new TicketItemModel(
                                    item,
                                    activeOrder.getCustomerName(item.getCustomerId()),
                                    t.getCreatedAt());
                            this.ticketItems.add(ticketItem);
                          }));
      activeOrder.getCustomers().stream()
          .map(c -> new OrderCustomerModel(c, activeOrder.calculateSubtotalByCustomer(c.getId())))
          .collect(Collectors.toList())
          .forEach(this.customers::add);
      activeOrder.getPayments().stream().map(PaymentModel::new).forEach(this.payments::add);
    }
  }

  public CustomerMenuModel(
      Restaurant restaurant,
      List<Category> categories,
      List<Item> items,
      Table table,
      java.time.ZoneId zoneId,
      org.springframework.context.MessageSource messageSource) {
    this(restaurant, categories, items, table, null, zoneId, messageSource);
  }

  public CustomerMenuModel(
      Restaurant restaurant,
      List<Category> categories,
      List<Item> items,
      java.time.ZoneId zoneId,
      org.springframework.context.MessageSource messageSource) {
    this(restaurant, categories, items, null, null, zoneId, messageSource);
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

  public boolean isReviewMode() {
    return reviewMode;
  }

  public java.util.UUID getOrderId() {
    return orderId;
  }

  public boolean isHasSentFeedback() {
    return hasSentFeedback;
  }

  public void setHasSentFeedback(boolean hasSentFeedback) {
    this.hasSentFeedback = hasSentFeedback;
  }

  public TableSummaryModel getTableSummary() {
    return tableSummary;
  }
}
