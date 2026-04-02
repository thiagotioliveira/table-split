package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.application.order.model.TicketItemRequest;
import dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TableStatusChangedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import java.util.UUID;

public class PlaceOrder {

  private final OpenTable openTable;
  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final EventPublisher eventPublisher;

  public PlaceOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      EventPublisher eventPublisher) {
    this.openTable = openTable;
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.eventPublisher = eventPublisher;
  }

  public Order execute(PlaceOrderRequest request) {
    Table table =
        tableRepository
            .findByRestaurantIdAndCod(request.getRestaurantId(), request.getTableCod())
            .orElseGet(() -> createTable(request));

    Order order =
        orderRepository
            .findActiveOrderByTableId(table.getId())
            .orElseGet(
                () ->
                    openTable.execute(
                        table.getId(),
                        request.getServiceFee() != null ? request.getServiceFee() : 0,
                        null,
                        null));

    // Register all participants first
    if (request.getCustomers() != null) {
      for (dev.thiagooliveira.tablesplit.application.order.model.CustomerRequest customer :
          request.getCustomers()) {
        order.addCustomer(customer.getId(), customer.getName());
      }
    }

    if (request.getTickets() != null) {
      for (dev.thiagooliveira.tablesplit.application.order.model.TicketRequest ticketRequest :
          request.getTickets()) {
        Ticket ticket = new Ticket();
        ticket.setNote(ticketRequest.getNote());
        for (TicketItemRequest itemRequest : ticketRequest.getItems()) {
          Item item =
              itemRepository
                  .findById(itemRequest.getItemId())
                  .orElseThrow(
                      () ->
                          new IllegalArgumentException(
                              "Item not found: " + itemRequest.getItemId()));

          TicketItem ticketItem =
              new TicketItem(
                  item,
                  itemRequest.getQuantity(),
                  itemRequest.getCustomerId(),
                  itemRequest.getNote());
          ticket.getItems().add(ticketItem);
        }
        order.addTicket(ticket);
        eventPublisher.publishEvent(new TicketCreatedEvent(order, ticket, table.getCod()));
      }
    }

    table.waiting();
    tableRepository.save(table);
    orderRepository.save(order);
    eventPublisher.publishEvent(new TableStatusChangedEvent(table));

    return order;
  }

  private Table createTable(PlaceOrderRequest request) {
    Table table = new Table(UUID.randomUUID(), request.getRestaurantId(), request.getTableCod());
    tableRepository.save(table);
    eventPublisher.publishEvent(new TableCreatedEvent(table));
    return table;
  }
}
