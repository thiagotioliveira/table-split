package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.*;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderService;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaceOrderTest {

  private static final UUID RESTAURANT_ID = UUID.randomUUID();
  private static final String TABLE_COD = "T1";
  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final String CUSTOMER_NAME = "Thiago";
  private static final UUID ITEM_ID = UUID.randomUUID();

  private OpenTable openTable;
  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private ItemRepository itemRepository;
  private EventPublisher eventPublisher;
  private SyncTableStatus syncTableStatus;
  private OrderService orderService;
  private PlaceOrder placeOrder;

  private Table table;
  private Order order;
  private Item item;

  @BeforeEach
  void setUp() {
    openTable = mock(OpenTable.class);
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    itemRepository = mock(ItemRepository.class);
    eventPublisher = mock(EventPublisher.class);
    syncTableStatus = mock(SyncTableStatus.class);
    orderService = mock(OrderService.class);
    placeOrder =
        new PlaceOrder(
            openTable,
            tableRepository,
            orderRepository,
            itemRepository,
            eventPublisher,
            syncTableStatus,
            orderService);

    table = new Table(UUID.randomUUID(), RESTAURANT_ID, TABLE_COD);
    order = new Order(UUID.randomUUID(), RESTAURANT_ID, table.getId(), 10);
    item = createDefaultItem();
  }

  @Test
  void shouldApplyCustomizationExtraPrice() {
    TicketItemRequest ticketItemRequest = createDefaultTicketItemRequest(1);

    dev.thiagooliveira.tablesplit.application.order.model.TicketItemOptionRequest option =
        new dev.thiagooliveira.tablesplit.application.order.model.TicketItemOptionRequest();
    option.setText("Vodka");
    option.setExtraPrice(new BigDecimal("2"));

    dev.thiagooliveira.tablesplit.application.order.model.TicketItemCustomizationRequest
        customization =
            new dev.thiagooliveira.tablesplit.application.order.model
                .TicketItemCustomizationRequest();
    customization.setTitle("Question?");
    customization.setOptions(List.of(option));

    ticketItemRequest.setCustomizations(List.of(customization));

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderRequest(ticketItemRequest));

    assertNotNull(result);
    assertEquals(new BigDecimal("102"), result.getItems().get(0).getTotalPrice());
  }

  @Test
  void shouldApplyFixedDiscount() {
    BigDecimal discountValue = new BigDecimal("15");
    UUID promotionId = UUID.randomUUID();
    DiscountType discountType = DiscountType.FIXED_VALUE;

    item.setPromotion(
        new Item.PromotionInfo(
            promotionId,
            item.getPrice().subtract(discountValue).max(BigDecimal.ZERO),
            discountType,
            discountValue));

    TicketItemRequest ticketItemRequest = createDefaultTicketItemRequest(1);
    ticketItemRequest.setPromotionId(promotionId);
    ticketItemRequest.setDiscountType(discountType.name());
    ticketItemRequest.setDiscountValue(discountValue);

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderRequest(ticketItemRequest));

    assertNotNull(result);
    assertEquals(new BigDecimal("85"), result.getItems().get(0).getTotalPrice());
  }

  @Test
  void shouldApplyPercentageDiscount() {
    BigDecimal discountValue = BigDecimal.TEN;
    UUID promotionId = UUID.randomUUID();
    DiscountType discountType = DiscountType.PERCENTAGE;

    item.setPromotion(
        new Item.PromotionInfo(
            promotionId,
            item.getPrice()
                .subtract(
                    item.getPrice()
                        .multiply(discountValue)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)),
            discountType,
            discountValue));

    TicketItemRequest ticketItemRequest = createDefaultTicketItemRequest(1);
    ticketItemRequest.setPromotionId(promotionId);
    ticketItemRequest.setDiscountType(discountType.name());
    ticketItemRequest.setDiscountValue(discountValue);

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderRequest(ticketItemRequest));

    assertNotNull(result);
    assertEquals(new BigDecimal("90.00"), result.getItems().get(0).getTotalPrice());
  }

  @Test
  void shouldPlaceOrderSuccessfully_whenTableIsAvailable() {
    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderRequest(createDefaultTicketItemRequest(2)));

    assertNotNull(result);
    assertEquals(CUSTOMER_ID, result.getItems().get(0).getCustomerId());
    assertEquals(2, result.getItems().get(0).getQuantity());
    verify(orderRepository).save(order);
  }

  @Test
  void shouldOpenTableAndPlaceOrder_whenTableIsAvailableButNoActiveOrder() {
    when(tableRepository.findByRestaurantIdAndCod(RESTAURANT_ID, TABLE_COD))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(table.getId())).thenReturn(Optional.empty());
    when(openTable.execute(table.getId(), 10, null, null)).thenReturn(order);
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

    Order result = placeOrder.execute(createPlaceOrderRequest(createDefaultTicketItemRequest(1)));

    assertNotNull(result);
    verify(openTable).execute(table.getId(), 10, null, null);
    verify(orderRepository).save(order);
  }

  // Helper Methods

  private Item createDefaultItem() {
    Item item = new Item();
    item.setId(ITEM_ID);
    item.setPrice(new BigDecimal("100"));
    item.setName(Map.of());
    return item;
  }

  private TicketItemRequest createDefaultTicketItemRequest(int quantity) {
    return new TicketItemRequest(ITEM_ID, CUSTOMER_ID, quantity, null);
  }

  private PlaceOrderRequest createPlaceOrderRequest(TicketItemRequest... items) {
    return new PlaceOrderRequest(
        RESTAURANT_ID,
        TABLE_COD,
        List.of(
            new dev.thiagooliveira.tablesplit.application.order.model.TicketRequest(
                null, List.of(items))),
        10,
        List.of(new CustomerRequest(CUSTOMER_ID, CUSTOMER_NAME)));
  }

  private void setupCommonMocks() {
    when(tableRepository.findByRestaurantIdAndCod(RESTAURANT_ID, TABLE_COD))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(table.getId())).thenReturn(Optional.of(order));
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
  }
}
