package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.OrderItemRequest;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.util.UUID;

public class PlaceOrder {

  private final OpenTable openTable;
  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;

  public PlaceOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository) {
    this.openTable = openTable;
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
  }

  public Order execute(PlaceOrderRequest request) {
    Table table =
        tableRepository
            .findByRestaurantIdAndCod(request.getRestaurantId(), request.getTableCod())
            .orElseGet(() -> createTable(request));

    Order order =
        orderRepository
            .findActiveOrderByTableId(table.getId())
            .orElseGet(() -> openTable.execute(table.getId(), request.getServiceFee()));

    for (OrderItemRequest itemRequest : request.getItems()) {
      Item item =
          itemRepository
              .findById(itemRequest.getItemId())
              .orElseThrow(
                  () -> new IllegalArgumentException("Item not found: " + itemRequest.getItemId()));
      order.addItem(
          item, itemRequest.getQuantity(), request.getCustomerName(), itemRequest.getNote());
    }

    orderRepository.save(order);

    return order;
  }

  private Table createTable(PlaceOrderRequest request) {
    Table table = new Table(UUID.randomUUID(), request.getRestaurantId(), request.getTableCod());
    tableRepository.save(table);
    return table;
  }
}
