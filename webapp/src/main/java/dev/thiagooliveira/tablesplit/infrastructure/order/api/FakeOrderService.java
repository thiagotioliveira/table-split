package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.application.order.RateItem;
import dev.thiagooliveira.tablesplit.application.order.SubmitGeneralFeedback;
import dev.thiagooliveira.tablesplit.application.order.command.CustomerCommand;
import dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemCustomizationCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemOptionCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FakeOrderService {

  private static final Logger logger = LoggerFactory.getLogger(FakeOrderService.class);

  private final FakeOrderProperties properties;
  private final RestaurantRepository restaurantRepository;
  private final TableRepository tableRepository;
  private final GetItem getItem;
  private final PlaceOrder placeOrder;
  private final ProcessPayment processPayment;
  private final SubmitGeneralFeedback submitGeneralFeedback;
  private final RateItem rateItem;

  @Value("${app.demo.restaurant-id}")
  private String demoRestaurantId;

  @Value("${app.time.zone-id}")
  private String zoneId;

  public FakeOrderService(
      FakeOrderProperties properties,
      RestaurantRepository restaurantRepository,
      TableRepository tableRepository,
      GetItem getItem,
      PlaceOrder placeOrder,
      ProcessPayment processPayment,
      SubmitGeneralFeedback submitGeneralFeedback,
      RateItem rateItem) {
    this.properties = properties;
    this.restaurantRepository = restaurantRepository;
    this.tableRepository = tableRepository;
    this.getItem = getItem;
    this.placeOrder = placeOrder;
    this.processPayment = processPayment;
    this.submitGeneralFeedback = submitGeneralFeedback;
    this.rateItem = rateItem;
  }

  @Async
  @Transactional
  public void generateFakeOrder() {
    UUID restaurantId = UUID.fromString(demoRestaurantId);
    logger.debug("Starting fake order generation for restaurant: {}", restaurantId);

    // Set Tenant Context
    String schema = TenantContext.generateTenantIdentifier(restaurantId);
    TenantContext.setCurrentTenant(schema);

    try {
      Restaurant restaurant =
          restaurantRepository
              .findById(restaurantId)
              .orElseThrow(
                  () -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

      if (!restaurant.isOpen(ZonedDateTime.now(ZoneId.of(zoneId)))) {
        logger.debug("Restaurant is closed. Skipping fake order generation.");
        return;
      }

      List<Table> tables = tableRepository.findAllByRestaurantId(restaurantId);
      List<Table> availableTables =
          tables.stream().filter(t -> t.getStatus() == TableStatus.AVAILABLE).toList();

      if (availableTables.isEmpty()) {
        logger.warn(
            "No available tables for restaurant: {}. Skipping fake order generation.",
            restaurantId);
        return;
      }

      Random random = new Random();
      Table table = availableTables.get(random.nextInt(availableTables.size()));
      String customerName =
          properties.getCustomerNames().get(random.nextInt(properties.getCustomerNames().size()));
      UUID customerId = UUID.randomUUID();

      List<Item> menuItems =
          getItem.execute(restaurantId, List.of(restaurant.getDefaultLanguage()), true);
      if (menuItems.isEmpty()) {
        logger.warn(
            "No menu items found for restaurant: {}. Skipping fake order generation.",
            restaurantId);
        return;
      }

      int numberOfItems = random.nextInt(3) + 1; // 1 to 3 different items
      List<TicketItemCommand> itemCommands = new ArrayList<>();
      for (int i = 0; i < numberOfItems; i++) {
        Item item = menuItems.get(random.nextInt(menuItems.size()));

        int quantity = random.nextInt(3) + 1; // 1 to 3 items of the same type
        UUID promotionId = null;
        String discountType = null;
        BigDecimal discountValue = BigDecimal.ZERO;

        if (item.getPromotion() != null) {
          promotionId = item.getPromotion().promotionId();
          discountType = item.getPromotion().discountType().name();
          discountValue = item.getPromotion().discountValue();
        }

        List<TicketItemCustomizationCommand> customizations =
            generateRandomCustomizations(item, restaurant.getDefaultLanguage(), random);

        itemCommands.add(
            new TicketItemCommand(
                item.getId(),
                customerId,
                quantity,
                null,
                promotionId,
                item.getPrice(),
                discountType,
                discountValue,
                customizations));
      }

      PlaceOrderCommand placeOrderCommand =
          new PlaceOrderCommand(
              restaurantId,
              table.getCod(),
              List.of(new TicketCommand(null, itemCommands)),
              restaurant.getServiceFee(),
              List.of(new CustomerCommand(customerId, customerName)));

      Order order = placeOrder.execute(placeOrderCommand);
      logger.debug("Fake order created: {} for table: {}", order.getId(), table.getCod());

      // Payment
      PaymentMethod paymentMethod =
          PaymentMethod.values()[random.nextInt(PaymentMethod.values().length)];
      processPayment.execute(
          table.getId(),
          customerId,
          order.calculateTotal(),
          paymentMethod,
          "Fake payment for demo");
      logger.debug("Fake payment processed for order: {}", order.getId());

      // Feedback
      int rating = random.nextInt(5) + 1; // 1 to 5
      List<String> comments = properties.getFeedbacks().get(rating);
      String comment = comments.get(random.nextInt(comments.size()));

      submitGeneralFeedback.execute(order.getId(), customerId, rating, comment);
      for (TicketItemCommand itemCommand : itemCommands) {
        rateItem.execute(itemCommand.itemId(), rating);
      }
      logger.debug("Fake feedback submitted for order: {} with rating: {}", order.getId(), rating);

    } catch (Exception e) {
      logger.error("Error generating fake order for restaurant: {}", restaurantId, e);
    } finally {
      TenantContext.clear();
    }
  }

  private List<TicketItemCustomizationCommand> generateRandomCustomizations(
      Item item, Language language, Random random) {
    if (item.getQuestions() == null || item.getQuestions().isEmpty()) {
      return null;
    }

    List<ItemQuestion> questions = item.getQuestions().get(language);
    if (questions == null || questions.isEmpty()) {
      return null;
    }

    List<TicketItemCustomizationCommand> customizations = new ArrayList<>();
    for (ItemQuestion question : questions) {
      boolean shouldSelect = question.isRequired() || random.nextBoolean();
      if (shouldSelect) {
        int min =
            question.getMinSelections() != null
                ? question.getMinSelections()
                : (question.isRequired() ? 1 : 0);
        int max = question.getMaxSelections() != null ? question.getMaxSelections() : 1;
        if (question.getType() == ItemQuestionType.SINGLE) {
          max = 1;
        }

        int count = min + random.nextInt(Math.max(1, max - min + 1));
        if (count > 0 && question.getOptions() != null && !question.getOptions().isEmpty()) {
          List<ItemOption> options = new ArrayList<>(question.getOptions());
          Collections.shuffle(options);
          List<TicketItemOptionCommand> selectedOptions =
              options.stream()
                  .limit(count)
                  .map(o -> new TicketItemOptionCommand(o.getText(), o.getExtraPrice()))
                  .toList();

          customizations.add(
              new TicketItemCustomizationCommand(question.getTitle(), selectedOptions));
        }
      }
    }
    return customizations.isEmpty() ? null : customizations;
  }
}
