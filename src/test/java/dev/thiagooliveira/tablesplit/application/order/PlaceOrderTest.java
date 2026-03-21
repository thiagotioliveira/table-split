package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.OrderItemRequest;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaceOrderTest {

  private OpenTable openTable;
  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private ItemRepository itemRepository;
  private EventPublisher eventPublisher;
  private PlaceOrder placeOrder;

  @BeforeEach
  void setUp() {
    openTable = mock(OpenTable.class);
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    itemRepository = mock(ItemRepository.class);
    eventPublisher = mock(EventPublisher.class);
    placeOrder =
        new PlaceOrder(openTable, tableRepository, orderRepository, itemRepository, eventPublisher);
  }

  @Test
  void shouldPlaceOrderSuccessfully_whenTableIsAvailable() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T1";
    String customerName = "Thiago";
    UUID itemId = UUID.randomUUID();

    Item item = new Item();
    item.setId(itemId);
    item.setPrice(BigDecimal.TEN);
    item.setName(Map.of());

    PlaceOrderRequest request =
        new PlaceOrderRequest(
            restaurantId,
            tableCod,
            List.of(
                new dev.thiagooliveira.tablesplit.application.order.model.TicketRequest(
                    customerName, null, List.of(new OrderItemRequest(itemId, 2, null)))),
            10);

    Table table = new Table(UUID.randomUUID(), restaurantId, tableCod);
    Order order = new Order(UUID.randomUUID(), restaurantId, table.getId(), 10);

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(table.getId())).thenReturn(Optional.of(order));
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    Order result = placeOrder.execute(request);

    assertNotNull(result);
    assertEquals(1, result.getTickets().size());
    assertEquals(customerName, result.getTickets().get(0).getItems().get(0).getCustomerName());
    assertEquals(2, result.getTickets().get(0).getItems().get(0).getQuantity());

    verify(orderRepository).save(order);
  }

  @Test
  void shouldOpenTableAndPlaceOrder_whenTableIsAvailableButNoActiveOrder() {
    UUID restaurantId = UUID.randomUUID();
    String tableCod = "T1";
    UUID itemId = UUID.randomUUID();

    Item item = new Item();
    item.setId(itemId);
    item.setPrice(BigDecimal.TEN);
    item.setName(Map.of());

    PlaceOrderRequest request =
        new PlaceOrderRequest(
            restaurantId,
            tableCod,
            List.of(
                new dev.thiagooliveira.tablesplit.application.order.model.TicketRequest(
                    "Customer", null, List.of(new OrderItemRequest(itemId, 1, null)))),
            10);

    Table table = new Table(UUID.randomUUID(), restaurantId, tableCod);
    Order newOrder = new Order(UUID.randomUUID(), restaurantId, table.getId(), 10);

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, tableCod))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(table.getId())).thenReturn(Optional.empty());
    when(openTable.execute(table.getId(), 10)).thenReturn(newOrder);
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    Order result = placeOrder.execute(request);

    assertNotNull(result);
    verify(openTable).execute(table.getId(), 10);
    verify(orderRepository).save(newOrder);
  }
}
