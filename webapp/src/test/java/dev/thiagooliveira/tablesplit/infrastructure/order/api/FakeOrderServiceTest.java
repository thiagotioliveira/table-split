package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.application.order.RateItem;
import dev.thiagooliveira.tablesplit.application.order.SubmitGeneralFeedback;
import dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

class FakeOrderServiceTest {

  private FakeOrderProperties properties;
  private RestaurantRepository restaurantRepository;
  private TableRepository tableRepository;
  private OrderRepository orderRepository;
  private GetItem getItem;
  private PlaceOrder placeOrder;
  private ProcessPayment processPayment;
  private SubmitGeneralFeedback submitGeneralFeedback;
  private RateItem rateItem;
  private TransactionTemplate transactionTemplate;
  private FakeOrderService fakeOrderService;

  private final UUID restaurantId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    properties = mock(FakeOrderProperties.class);
    restaurantRepository = mock(RestaurantRepository.class);
    tableRepository = mock(TableRepository.class);
    orderRepository = mock(OrderRepository.class);
    getItem = mock(GetItem.class);
    placeOrder = mock(PlaceOrder.class);
    processPayment = mock(ProcessPayment.class);
    submitGeneralFeedback = mock(SubmitGeneralFeedback.class);
    rateItem = mock(RateItem.class);
    transactionTemplate = mock(TransactionTemplate.class);

    // Mock TransactionTemplate.executeWithoutResult to run the callback immediately
    doAnswer(
            invocation -> {
              Consumer<TransactionStatus> callback = invocation.getArgument(0);
              callback.accept(mock(TransactionStatus.class));
              return null;
            })
        .when(transactionTemplate)
        .executeWithoutResult(any());

    fakeOrderService =
        new FakeOrderService(
            properties,
            restaurantRepository,
            tableRepository,
            orderRepository,
            getItem,
            placeOrder,
            processPayment,
            submitGeneralFeedback,
            rateItem,
            transactionTemplate);

    ReflectionTestUtils.setField(fakeOrderService, "demoRestaurantId", restaurantId.toString());
    ReflectionTestUtils.setField(fakeOrderService, "zoneId", "Europe/Lisbon");

    when(properties.getCustomerNames()).thenReturn(List.of("Fake Customer"));
    when(properties.getFeedbacks())
        .thenReturn(
            Map.of(
                1, List.of("Bad"),
                2, List.of("Poor"),
                3, List.of("Ok"),
                4, List.of("Good"),
                5, List.of("Excellent!")));
  }

  @Test
  void shouldGenerateFakeOrderSuccessfully() {
    Restaurant restaurant = mock(Restaurant.class);
    when(restaurant.getId()).thenReturn(restaurantId);
    when(restaurant.isOpen(any(ZonedDateTime.class))).thenReturn(true);
    when(restaurant.getServiceFee()).thenReturn(10);
    when(restaurant.getDefaultLanguage()).thenReturn(Language.PT);
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

    Table table = new Table(UUID.randomUUID(), restaurantId, "T1");
    when(tableRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(table));

    Item item = new Item();
    item.setId(UUID.randomUUID());
    item.setPrice(new BigDecimal("50"));
    item.setQuestions(Map.of());
    when(getItem.execute(any(UUID.class), anyList(), anyBoolean())).thenReturn(List.of(item));

    Order order = mock(Order.class);
    when(order.getId()).thenReturn(UUID.randomUUID());
    when(order.calculateTotal()).thenReturn(new BigDecimal("55"));
    when(placeOrder.execute(any(PlaceOrderCommand.class))).thenReturn(order);

    Ticket ticket = mock(Ticket.class);
    TicketItem ticketItem = mock(TicketItem.class);
    when(ticketItem.getId()).thenReturn(UUID.randomUUID());
    when(ticket.getItems()).thenReturn(List.of(ticketItem));
    when(order.getTickets()).thenReturn(List.of(ticket));

    fakeOrderService.generateFakeOrder();

    verify(placeOrder).execute(any(PlaceOrderCommand.class));
    verify(processPayment)
        .execute(
            eq(table.getId()),
            any(UUID.class),
            eq(new BigDecimal("55")),
            any(),
            anyString(),
            any(Language.class),
            any(UUID.class));
    verify(submitGeneralFeedback)
        .execute(eq(order.getId()), any(UUID.class), anyInt(), anyString());
    verify(rateItem, atLeastOnce()).execute(any(UUID.class), anyInt());
    verify(orderRepository).save(order);
  }

  @Test
  void shouldNotGenerateOrderIfRestaurantIsClosed() {
    Restaurant restaurant = mock(Restaurant.class);
    when(restaurant.isOpen(any(ZonedDateTime.class))).thenReturn(false);
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

    fakeOrderService.generateFakeOrder();

    verifyNoInteractions(placeOrder);
  }

  @Test
  void shouldNotGenerateOrderIfNoAvailableTables() {
    Restaurant restaurant = mock(Restaurant.class);
    when(restaurant.isOpen(any(ZonedDateTime.class))).thenReturn(true);
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

    Table table = new Table(UUID.randomUUID(), restaurantId, "T1");
    table.occupy(); // Not AVAILABLE
    when(tableRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(table));

    fakeOrderService.generateFakeOrder();

    verifyNoInteractions(placeOrder);
  }
}
