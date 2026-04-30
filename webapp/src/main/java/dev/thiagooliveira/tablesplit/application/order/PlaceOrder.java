package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand;
import dev.thiagooliveira.tablesplit.application.order.command.TicketItemCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import java.util.UUID;

public class PlaceOrder {

  private final OpenTable openTable;
  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final SyncTableStatus syncTableStatus;
  private final OrderService orderService;

  public PlaceOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      SyncTableStatus syncTableStatus,
      OrderService orderService) {
    this.openTable = openTable;
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.syncTableStatus = syncTableStatus;
    this.orderService = orderService;
  }

  public Order execute(PlaceOrderCommand command) {
    Table table = getOrCreateTable(command);

    Order order =
        orderRepository
            .findActiveOrderByTableId(table.getId())
            .orElseGet(() -> openNewSession(table, command));

    registerParticipants(order, command);
    processTickets(order, command, table.getCod());

    orderRepository.save(order);
    syncTableStatus.execute(order);

    return order;
  }

  private Table getOrCreateTable(PlaceOrderCommand command) {
    return tableRepository
        .findByRestaurantIdAndCod(command.restaurantId(), command.tableCod())
        .orElseGet(
            () -> {
              Table table =
                  Table.create(UUID.randomUUID(), command.restaurantId(), command.tableCod());
              tableRepository.save(table);
              return table;
            });
  }

  private Order openNewSession(Table table, PlaceOrderCommand command) {
    // Validate session for participants in the command
    if (command.tickets() != null) {
      command.tickets().stream()
          .flatMap(t -> t.items().stream())
          .map(TicketItemCommand::customerId)
          .filter(java.util.Objects::nonNull)
          .distinct()
          .forEach(customerId -> orderService.validateCustomerSession(table.getId(), customerId));
    }

    return openTable.execute(
        table.getId(), command.serviceFee() != null ? command.serviceFee() : 0, null, null);
  }

  private void registerParticipants(Order order, PlaceOrderCommand command) {
    if (command.customers() != null) {
      command.customers().forEach(c -> order.addCustomer(c.id(), c.name()));
    }
  }

  private void processTickets(Order order, PlaceOrderCommand command, String tableCod) {
    if (command.tickets() == null) return;

    for (var ticketCommand : command.tickets()) {
      java.util.List<TicketItem> items =
          ticketCommand.items().stream().map(this::mapToTicketItem).toList();

      order.addTicketWithItems(items, ticketCommand.note(), tableCod);
    }
  }

  private TicketItem mapToTicketItem(TicketItemCommand itemCommand) {
    Item item =
        itemRepository
            .findById(itemCommand.itemId())
            .orElseThrow(
                () -> new IllegalArgumentException("Item not found: " + itemCommand.itemId()));

    return new TicketItem(
        item,
        itemCommand.quantity(),
        itemCommand.customerId(),
        itemCommand.note(),
        mapCustomizations(itemCommand),
        itemCommand.promotionId(),
        itemCommand.discountType(),
        itemCommand.discountValue());
  }

  private java.util.List<TicketItemCustomization> mapCustomizations(TicketItemCommand itemCommand) {
    if (itemCommand.customizations() == null) return java.util.List.of();

    return itemCommand.customizations().stream()
        .map(
            c ->
                new TicketItemCustomization(
                    c.title(),
                    c.options().stream()
                        .map(o -> new TicketItemOption(o.text(), o.extraPrice()))
                        .toList()))
        .toList();
  }
}
