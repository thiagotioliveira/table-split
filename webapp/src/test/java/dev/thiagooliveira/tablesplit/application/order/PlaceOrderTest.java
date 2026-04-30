package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.order.command.*;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderService;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
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
    syncTableStatus = mock(SyncTableStatus.class);
    orderService = mock(OrderService.class);
    placeOrder =
        new PlaceOrder(
            openTable,
            tableRepository,
            orderRepository,
            itemRepository,
            syncTableStatus,
            orderService);

    table = new Table(UUID.randomUUID(), RESTAURANT_ID, TABLE_COD);
    order = new Order(UUID.randomUUID(), RESTAURANT_ID, table.getId(), 10);
    item = createDefaultItem();
  }

  @Test
  void shouldApplyCustomizationExtraPrice() {
    TicketItemOptionCommand option = new TicketItemOptionCommand("Vodka", new BigDecimal("2"));

    TicketItemCustomizationCommand customization =
        new TicketItemCustomizationCommand("Question?", List.of(option));

    TicketItemCommand ticketItemCommand =
        new TicketItemCommand(
            ITEM_ID, CUSTOMER_ID, 1, null, null, null, null, null, List.of(customization));

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderCommand(ticketItemCommand));

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

    TicketItemCommand ticketItemCommand =
        new TicketItemCommand(
            ITEM_ID,
            CUSTOMER_ID,
            1,
            null,
            promotionId,
            null,
            discountType.name(),
            discountValue,
            null);

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderCommand(ticketItemCommand));

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

    TicketItemCommand ticketItemCommand =
        new TicketItemCommand(
            ITEM_ID,
            CUSTOMER_ID,
            1,
            null,
            promotionId,
            null,
            discountType.name(),
            discountValue,
            null);

    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderCommand(ticketItemCommand));

    assertNotNull(result);
    assertEquals(new BigDecimal("90.00"), result.getItems().get(0).getTotalPrice());
  }

  @Test
  void shouldPlaceOrderSuccessfully_whenTableIsAvailable() {
    setupCommonMocks();

    Order result = placeOrder.execute(createPlaceOrderCommand(createDefaultTicketItemCommand(2)));

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

    Order result = placeOrder.execute(createPlaceOrderCommand(createDefaultTicketItemCommand(1)));

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

  private TicketItemCommand createDefaultTicketItemCommand(int quantity) {
    return new TicketItemCommand(
        ITEM_ID, CUSTOMER_ID, quantity, null, null, null, null, null, null);
  }

  private PlaceOrderCommand createPlaceOrderCommand(TicketItemCommand... items) {
    return new PlaceOrderCommand(
        RESTAURANT_ID,
        TABLE_COD,
        List.of(new TicketCommand(null, List.of(items))),
        10,
        List.of(new CustomerCommand(CUSTOMER_ID, CUSTOMER_NAME)));
  }

  private void setupCommonMocks() {
    when(tableRepository.findByRestaurantIdAndCod(RESTAURANT_ID, TABLE_COD))
        .thenReturn(Optional.of(table));
    when(orderRepository.findActiveOrderByTableId(table.getId())).thenReturn(Optional.of(order));
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
  }
}
