package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.application.account.GetAccount;
import dev.thiagooliveira.tablesplit.application.order.CallWaiter;
import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.RateItem;
import dev.thiagooliveira.tablesplit.application.order.SubmitGeneralFeedback;
import dev.thiagooliveira.tablesplit.application.order.UpdateCustomerName;
import dev.thiagooliveira.tablesplit.application.order.command.CustomerCommand;
import dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemCustomizationCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemOptionCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.api.CustomerMenuApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.CustomerOpenTableRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.CustomerPlaceOrderRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.GeneralFeedbackRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.RateItemRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.TableDataResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.UpdateCustomerNameRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerApiController implements CustomerMenuApi {

  private final GetRestaurant getRestaurant;
  private final GetTables getTables;
  private final OpenTable openTable;
  private final PlaceOrder placeOrder;
  private final GetOrder getOrder;
  private final CallWaiter callWaiter;
  private final RateItem rateItem;
  private final SubmitGeneralFeedback submitGeneralFeedback;
  private final UpdateCustomerName updateCustomerName;
  private final OrderRepository orderRepository;
  private final TransactionalContext transactionalContext;
  private final CustomerApiMapper mapper;
  private final GetAccount getAccount;

  public CustomerApiController(
      GetRestaurant getRestaurant,
      GetTables getTables,
      OpenTable openTable,
      PlaceOrder placeOrder,
      GetOrder getOrder,
      CallWaiter callWaiter,
      RateItem rateItem,
      SubmitGeneralFeedback submitGeneralFeedback,
      UpdateCustomerName updateCustomerName,
      OrderRepository orderRepository,
      TransactionalContext transactionalContext,
      CustomerApiMapper mapper,
      GetAccount getAccount) {
    this.getRestaurant = getRestaurant;
    this.getTables = getTables;
    this.openTable = openTable;
    this.placeOrder = placeOrder;
    this.getOrder = getOrder;
    this.callWaiter = callWaiter;
    this.rateItem = rateItem;
    this.submitGeneralFeedback = submitGeneralFeedback;
    this.updateCustomerName = updateCustomerName;
    this.orderRepository = orderRepository;
    this.transactionalContext = transactionalContext;
    this.mapper = mapper;
    this.getAccount = getAccount;
  }

  @Override
  public ResponseEntity<TableDataResponse> getTableData(
      String slug, String tableCode, String lastOrderId, String customerId) {
    Restaurant restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("Restaurant not found"));

    var table =
        getTables
            .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
            .orElseThrow(() -> new NotFoundException("Table not found"));

    var order =
        orderRepository
            .findActiveOrderByTableId(table.getId())
            .or(
                () -> {
                  if (lastOrderId != null && !lastOrderId.isBlank()) {
                    try {
                      return getOrder.findById(UUID.fromString(lastOrderId));
                    } catch (Exception e) {
                      return java.util.Optional.empty();
                    }
                  }
                  return java.util.Optional.empty();
                });

    TableDataResponse response = new TableDataResponse();
    Language lang = Language.fromLocale(Locale.getDefault());

    order.ifPresent(
        o -> {
          response.setOrderId(o.getId());
          response.setReviewMode(o.getStatus() == OrderStatus.CLOSED);
          response.setHasSentFeedback(false); // TODO: check feedback
          response.setTicketItems(mapper.mapToSimpleTicketItems(o, lang));
          response.setCustomers(mapper.mapToOrderCustomerModels(o));
          response.setPayments(mapper.mapToPaymentModels(o));
          response.setTableSummary(mapper.mapToTableSummaryModel(o));
        });

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> openTable(
      String slug, String tableCode, CustomerOpenTableRequest request) {
    Restaurant restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("Restaurant not found"));

    var table =
        getTables
            .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
            .orElseThrow(() -> new NotFoundException("Table not found"));

    transactionalContext.execute(
        () ->
            openTable.execute(
                table.getId(),
                restaurant.getServiceFee(),
                request.getCustomerId(),
                request.getCustomerName()));

    var customerIdCookie =
        org.springframework.http.ResponseCookie.from(
                "ts_customer_id", request.getCustomerId().toString())
            .path("/")
            .maxAge(java.time.Duration.ofDays(7))
            .sameSite("Lax")
            .build();

    return ResponseEntity.ok()
        .header(org.springframework.http.HttpHeaders.SET_COOKIE, customerIdCookie.toString())
        .build();
  }

  @Override
  public ResponseEntity<Void> placeOrder(
      String slug, String tableCode, CustomerPlaceOrderRequest request) {
    Restaurant restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("Restaurant not found"));

    var command =
        new PlaceOrderCommand(
            restaurant.getId(),
            tableCode,
            request.getTickets().stream()
                .map(
                    t ->
                        new TicketCommand(
                            "", // note from cart is not at ticket level in this request
                            t.getItems().stream()
                                .map(
                                    i ->
                                        new TicketItemCommand(
                                            i.getItemId(),
                                            t.getCustomerId(),
                                            i.getQuantity(),
                                            i.getNote(),
                                            null, // promotion
                                            null,
                                            null,
                                            null,
                                            i.getCustomizations() != null
                                                ? i.getCustomizations().stream()
                                                    .map(
                                                        c ->
                                                            new TicketItemCustomizationCommand(
                                                                c.getTitle(),
                                                                c.getOptions().stream()
                                                                    .map(
                                                                        o ->
                                                                            new TicketItemOptionCommand(
                                                                                o.getText(),
                                                                                o.getExtraPrice()
                                                                                        != null
                                                                                    ? java.math
                                                                                        .BigDecimal
                                                                                        .valueOf(
                                                                                            o
                                                                                                .getExtraPrice())
                                                                                    : null))
                                                                    .collect(Collectors.toList())))
                                                    .collect(Collectors.toList())
                                                : null))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList()),
            restaurant.getServiceFee(),
            request.getCustomers() != null
                ? request.getCustomers().stream()
                    .map(c -> new CustomerCommand(c.getId(), c.getName()))
                    .collect(Collectors.toList())
                : List.of());

    transactionalContext.execute(() -> placeOrder.execute(command));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> callWaiter(String slug, String tableCode, String customerId) {
    Restaurant restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("Restaurant not found"));

    transactionalContext.execute(
        () ->
            callWaiter.execute(
                restaurant.getId(),
                tableCode,
                customerId != null ? UUID.fromString(customerId) : null));

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> rateItem(String slug, String tableCode, RateItemRequest request) {
    transactionalContext.execute(() -> rateItem.execute(request.getItemId(), request.getRating()));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> submitGeneralFeedback(
      String slug, String tableCode, GeneralFeedbackRequest request) {
    transactionalContext.execute(
        () ->
            submitGeneralFeedback.execute(
                request.getOrderId(),
                request.getCustomerId(),
                request.getRating(),
                request.getComment()));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateCustomerName(
      String slug, String tableCode, UpdateCustomerNameRequest updateCustomerNameRequest) {
    Restaurant restaurant =
        getRestaurant
            .execute(slug)
            .orElseThrow(() -> new NotFoundException("Restaurant not found"));

    var table =
        getTables
            .findByRestaurantIdAndCod(restaurant.getId(), tableCode)
            .orElseThrow(() -> new NotFoundException("Table not found"));

    transactionalContext.execute(
        () ->
            updateCustomerName.execute(
                table.getId(),
                updateCustomerNameRequest.getCustomerId(),
                updateCustomerNameRequest.getName()));

    return ResponseEntity.ok().build();
  }
}
