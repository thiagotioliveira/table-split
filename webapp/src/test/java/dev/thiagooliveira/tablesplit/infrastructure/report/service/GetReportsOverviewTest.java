package dev.thiagooliveira.tablesplit.infrastructure.report.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.report.spec.v1.model.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetReportsOverviewTest {

  private OrderRepository orderRepository;
  private FeedbackRepository feedbackRepository;
  private TableRepository tableRepository;
  private ItemRepository itemRepository;
  private PromotionRepository promotionRepository;
  private RestaurantRepository restaurantRepository;
  private GetReportsOverview getReportsOverview;

  @BeforeEach
  void setUp() {
    // Initialize timezone static context
    new Time("UTC");

    orderRepository = mock(OrderRepository.class);
    feedbackRepository = mock(FeedbackRepository.class);
    tableRepository = mock(TableRepository.class);
    itemRepository = mock(ItemRepository.class);
    promotionRepository = mock(PromotionRepository.class);
    restaurantRepository = mock(RestaurantRepository.class);
    getReportsOverview =
        new GetReportsOverview(
            orderRepository,
            feedbackRepository,
            tableRepository,
            itemRepository,
            promotionRepository,
            restaurantRepository);
  }

  @Test
  void shouldCalculateReportOverviewSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID tableId = UUID.randomUUID();
    UUID itemId1 = UUID.randomUUID();
    UUID itemId2 = UUID.randomUUID();
    UUID promoId = UUID.randomUUID();

    ZonedDateTime now = ZonedDateTime.now(java.time.ZoneOffset.UTC);

    // Mock Restaurant
    Restaurant restaurant = new Restaurant();
    restaurant.setCurrency(Currency.BRL);
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

    // Mock Table count
    when(tableRepository.count(restaurantId)).thenReturn(5L);

    // Mock Items
    Category cat1 = new Category();
    Map<dev.thiagooliveira.tablesplit.domain.common.Language, String> catNameMap =
        new EnumMap<>(dev.thiagooliveira.tablesplit.domain.common.Language.class);
    catNameMap.put(dev.thiagooliveira.tablesplit.domain.common.Language.PT, "Bebidas");
    cat1.setName(catNameMap);

    Item item1 = new Item();
    item1.setId(itemId1);
    item1.setCategory(cat1);
    item1.addImage(UUID.randomUUID(), "http://image1.jpg", true);

    Item item2 = new Item();
    item2.setId(itemId2);
    item2.addImage(UUID.randomUUID(), "http://image2.jpg", true); // Category null for "Outros"

    when(itemRepository.findAllById(anySet())).thenReturn(List.of(item1, item2));

    // Mock Promotions
    Promotion promo = new Promotion();
    promo.setId(promoId);
    promo.setName("Desconto Especial");
    when(promotionRepository.findAllById(anySet())).thenReturn(List.of(promo));

    // Mock Feedbacks Distribution
    Map<Integer, Long> distribution = new HashMap<>();
    distribution.put(5, 10L);
    distribution.put(4, 5L);
    when(feedbackRepository.getRatingDistribution(eq(restaurantId), any()))
        .thenReturn(distribution);

    // Create Current Period Orders
    Order order1 = new Order(UUID.randomUUID(), restaurantId, tableId, 1);
    order1.setOpenedAt(now.minusDays(2));
    order1.setClosedAt(now.minusDays(2).plusHours(2));

    Ticket ticket1 = new Ticket();
    TicketItem tItem1 = new TicketItem();
    tItem1.setItemId(itemId1);
    Map<dev.thiagooliveira.tablesplit.domain.common.Language, String> nameMap1 =
        new EnumMap<>(dev.thiagooliveira.tablesplit.domain.common.Language.class);
    nameMap1.put(dev.thiagooliveira.tablesplit.domain.common.Language.PT, "Suco");
    tItem1.setName(nameMap1);
    tItem1.setUnitPrice(BigDecimal.valueOf(15.00));
    tItem1.setQuantity(2);
    tItem1.setPromotionSnapshot(
        new TicketItem.PromotionSnapshot(
            promoId, BigDecimal.valueOf(20.00), "FIXED_VALUE", BigDecimal.valueOf(5.00)));
    ticket1.getItems().add(tItem1);

    TicketItem tItem2 = new TicketItem();
    tItem2.setItemId(itemId2);
    Map<dev.thiagooliveira.tablesplit.domain.common.Language, String> nameMap2 =
        new EnumMap<>(dev.thiagooliveira.tablesplit.domain.common.Language.class);
    nameMap2.put(dev.thiagooliveira.tablesplit.domain.common.Language.PT, "Pastel");
    tItem2.setName(nameMap2);
    tItem2.setUnitPrice(BigDecimal.valueOf(10.00));
    tItem2.setQuantity(1);
    ticket1.getItems().add(tItem2);

    order1.addTicket(ticket1);

    // Payment for order 1
    Payment payment1 =
        new Payment(
            UUID.randomUUID(),
            order1.getId(),
            UUID.randomUUID(),
            BigDecimal.valueOf(40.00),
            PaymentMethod.CARD,
            "Pago");
    payment1.setPaidAt(now.minusDays(2));
    order1.getPayments().add(payment1);

    // Current Open Order (to test open orders addition)
    Order openOrder = new Order(UUID.randomUUID(), restaurantId, tableId, 2);
    openOrder.setOpenedAt(now.minusDays(1));
    // No payments, but some participants
    openOrder.getCustomers().add(new OrderCustomer());

    when(orderRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
            eq(restaurantId), eq(OrderStatus.CLOSED), any(), any()))
        .thenReturn(List.of(order1));
    when(orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN))
        .thenReturn(List.of(openOrder));

    // Call execution
    ReportsOverviewResponse response = getReportsOverview.execute(restaurantId, 7);

    assertNotNull(response);
    assertEquals("R$", response.getCurrencySymbol());
    assertNotNull(response.getStats());
    assertEquals(40.0, response.getStats().getTotalRevenue());
    assertEquals(1, response.getStats().getTotalOrders());

    // Occupancy
    assertNotNull(response.getTableOccupancy());
    assertEquals(5, response.getTableOccupancy().getTotalTablesAvailable());
    assertEquals(
        120, response.getTableOccupancy().getAverageDurationMinutes()); // 2 hours = 120 mins
    assertEquals(1, response.getTableOccupancy().getPeakSimultaneousTables());

    // Category Sales
    assertNotNull(response.getCategorySales());
    assertFalse(response.getCategorySales().isEmpty());

    // Top Items
    assertNotNull(response.getTopItems());
    assertFalse(response.getTopItems().isEmpty());

    // Peak Hours
    assertNotNull(response.getPeakHours());
    assertEquals(24, response.getPeakHours().size());

    // Payment Methods
    assertNotNull(response.getPaymentMethods());
    assertFalse(response.getPaymentMethods().isEmpty());

    // Promo Usage
    assertNotNull(response.getPromoUsage());
    assertFalse(response.getPromoUsage().isEmpty());

    // Customer Ratings
    assertNotNull(response.getCustomerRatings());
    assertEquals(15, response.getCustomerRatings().getTotalReviews());
    assertEquals(4.666, response.getCustomerRatings().getAverageRating(), 0.01);
  }
}
