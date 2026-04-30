package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.application.order.model.TicketItemRequest;
import dev.thiagooliveira.tablesplit.domain.event.TableCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.TicketCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.order.*;
import java.util.UUID;

public class PlaceOrder {

  private final OpenTable openTable;
  private final TableRepository tableRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final EventPublisher eventPublisher;
  private final SyncTableStatus syncTableStatus;

  public PlaceOrder(
      OpenTable openTable,
      TableRepository tableRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      EventPublisher eventPublisher,
      SyncTableStatus syncTableStatus) {
    this.openTable = openTable;
    this.tableRepository = tableRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.eventPublisher = eventPublisher;
    this.syncTableStatus = syncTableStatus;
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
                () -> {
                  // Business Rule: Check if the customer was in the last closed session
                  orderRepository.findAllByTableIdOrderByOpenedAtDesc(table.getId()).stream()
                      .findFirst()
                      .ifPresent(
                          lastOrder -> {
                            if (lastOrder.getStatus()
                                == dev.thiagooliveira.tablesplit.domain.order.OrderStatus.CLOSED) {
                              // We check if the customer in the request was in the last order
                              // This assumes the first customer mentioned in the request (or any)
                              // For simplicity and to match the user's issue, we'll check against
                              // all ticket customers
                              boolean wasParticipant = false;
                              if (request.getTickets() != null) {
                                for (var ticket : request.getTickets()) {
                                  for (var item : ticket.getItems()) {
                                    if (item.getCustomerId() != null
                                        && lastOrder.getCustomers().stream()
                                            .anyMatch(
                                                c -> c.getId().equals(item.getCustomerId()))) {
                                      wasParticipant = true;
                                      break;
                                    }
                                  }
                                }
                              }

                              if (wasParticipant) {
                                throw new TableSessionClosedException(
                                    "Table is closed. Please complete your feedback before starting a new session.");
                              }
                            }
                          });

                  return openTable.execute(
                      table.getId(),
                      request.getServiceFee() != null ? request.getServiceFee() : 0,
                      null,
                      null);
                });

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

          UUID customerId = itemRequest.getCustomerId();
          if (customerId == null) {
            if (order.getCustomers().isEmpty()) {
              order.addCustomer(UUID.randomUUID(), "Atendimento " + table.getCod());
            }
            customerId = order.getCustomers().iterator().next().getId();
          }

          TicketItem ticketItem =
              new TicketItem(
                  item,
                  itemRequest.getQuantity(),
                  customerId,
                  itemRequest.getNote(),
                  itemRequest.getCustomizations() != null
                      ? itemRequest.getCustomizations().stream()
                          .map(
                              c ->
                                  new TicketItemCustomization(
                                      c.getTitle(),
                                      c.getOptions().stream()
                                          .map(
                                              o ->
                                                  new TicketItemOption(
                                                      o.getText(), o.getExtraPrice()))
                                          .toList()))
                          .toList()
                      : java.util.List.of(),
                  itemRequest.getPromotionId(),
                  itemRequest.getDiscountType(),
                  itemRequest.getDiscountValue());
          ticket.getItems().add(ticketItem);
        }
        order.addTicket(ticket);
        eventPublisher.publishEvent(new TicketCreatedEvent(order, ticket, table.getCod()));
      }
    }

    orderRepository.save(order);
    syncTableStatus.execute(order);

    return order;
  }

  private Table createTable(PlaceOrderRequest request) {
    Table table = new Table(UUID.randomUUID(), request.getRestaurantId(), request.getTableCod());
    tableRepository.save(table);
    eventPublisher.publishEvent(new TableCreatedEvent(table));
    return table;
  }
}
