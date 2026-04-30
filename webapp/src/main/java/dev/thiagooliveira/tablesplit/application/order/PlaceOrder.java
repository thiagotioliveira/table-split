package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.application.order.model.TicketItemRequest;
import dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import java.util.UUID;

public class PlaceOrder {

  private final OpenTable openTable;
  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final EventPublisher eventPublisher;
  private final SyncTableStatus syncTableStatus;
  private final OrderService orderService;

  public PlaceOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus,
      OrderService orderService) {
    this.openTable = openTable;
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.eventPublisher = eventPublisher;
    this.syncTableStatus = syncTableStatus;
    this.orderService = orderService;
  }

  public Order execute(PlaceOrderRequest request) {
    Table table = getOrCreateTable(request);

    Order order =
        orderRepository
            .findActiveOrderByTableId(table.getId())
            .orElseGet(() -> openNewSession(table, request));

    registerParticipants(order, request);
    processTickets(order, request, table.getCod());

    orderRepository.save(order);
    syncTableStatus.execute(order);

    return order;
  }

  private Table getOrCreateTable(PlaceOrderRequest request) {
    return tableRepository
        .findByRestaurantIdAndCod(request.getRestaurantId(), request.getTableCod())
        .orElseGet(
            () -> {
              Table table =
                  new Table(UUID.randomUUID(), request.getRestaurantId(), request.getTableCod());
              tableRepository.save(table);
              eventPublisher.publishEvent(new TableCreatedEvent(table));
              return table;
            });
  }

  private Order openNewSession(Table table, PlaceOrderRequest request) {
    // Validate session for participants in the request
    if (request.getTickets() != null) {
      request.getTickets().stream()
          .flatMap(t -> t.getItems().stream())
          .map(TicketItemRequest::getCustomerId)
          .filter(java.util.Objects::nonNull)
          .distinct()
          .forEach(customerId -> orderService.validateCustomerSession(table.getId(), customerId));
    }

    return openTable.execute(
        table.getId(), request.getServiceFee() != null ? request.getServiceFee() : 0, null, null);
  }

  private void registerParticipants(Order order, PlaceOrderRequest request) {
    if (request.getCustomers() != null) {
      request.getCustomers().forEach(c -> order.addCustomer(c.getId(), c.getName()));
    }
  }

  private void processTickets(Order order, PlaceOrderRequest request, String tableCod) {
    if (request.getTickets() == null) return;

    for (var ticketRequest : request.getTickets()) {
      java.util.List<TicketItem> items =
          ticketRequest.getItems().stream().map(this::mapToTicketItem).toList();

      order.addTicketWithItems(items, ticketRequest.getNote());

      // We need to find the ticket we just added to publish the event
      // This is slightly awkward but better than manual creation here
      Ticket ticket = order.getTickets().get(order.getTickets().size() - 1);
      eventPublisher.publishEvent(new TicketCreatedEvent(order, ticket, tableCod));
    }
  }

  private TicketItem mapToTicketItem(TicketItemRequest itemRequest) {
    Item item =
        itemRepository
            .findById(itemRequest.getItemId())
            .orElseThrow(
                () -> new IllegalArgumentException("Item not found: " + itemRequest.getItemId()));

    return new TicketItem(
        item,
        itemRequest.getQuantity(),
        itemRequest.getCustomerId(),
        itemRequest.getNote(),
        mapCustomizations(itemRequest),
        itemRequest.getPromotionId(),
        itemRequest.getDiscountType(),
        itemRequest.getDiscountValue());
  }

  private java.util.List<TicketItemCustomization> mapCustomizations(TicketItemRequest itemRequest) {
    if (itemRequest.getCustomizations() == null) return java.util.List.of();

    return itemRequest.getCustomizations().stream()
        .map(
            c ->
                new TicketItemCustomization(
                    c.getTitle(),
                    c.getOptions().stream()
                        .map(o -> new TicketItemOption(o.getText(), o.getExtraPrice()))
                        .toList()))
        .toList();
  }
}
