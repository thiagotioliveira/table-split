package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GroupedStatusItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GroupedStatusWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.StatusGroupItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class OngoingOrdersWidgetProvider implements WidgetProvider {

  private final OrderRepository orderRepository;
  private final dev.thiagooliveira.tablesplit.domain.order.TableRepository tableRepository;
  private final org.springframework.context.MessageSource messageSource;

  public OngoingOrdersWidgetProvider(
      OrderRepository orderRepository,
      dev.thiagooliveira.tablesplit.domain.order.TableRepository tableRepository,
      org.springframework.context.MessageSource messageSource) {
    this.orderRepository = orderRepository;
    this.tableRepository = tableRepository;
    this.messageSource = messageSource;
  }

  @Override
  public Module getRequiredModule() {
    return Module.ORDERS;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    List<Order> openOrders =
        orderRepository.findAllByRestaurantIdAndStatus(restaurantId, OrderStatus.OPEN);

    int pendingCount = 0;
    int preparingCount = 0;
    int readyCount = 0;

    record TicketWithOrder(Ticket ticket, Order order) {}
    List<TicketWithOrder> ongoingTickets = new ArrayList<>();

    for (Order order : openOrders) {
      for (Ticket ticket : order.getTickets()) {
        if (ticket.getStatus() == TicketStatus.PENDING) {
          pendingCount++;
          ongoingTickets.add(new TicketWithOrder(ticket, order));
        } else if (ticket.getStatus() == TicketStatus.PREPARING) {
          preparingCount++;
          ongoingTickets.add(new TicketWithOrder(ticket, order));
        } else if (ticket.getStatus() == TicketStatus.READY) {
          readyCount++;
          ongoingTickets.add(new TicketWithOrder(ticket, order));
        }
      }
    }

    // Sort by opened time (newest first)
    ongoingTickets.sort(
        Comparator.comparing((TicketWithOrder t) -> t.ticket().getCreatedAt()).reversed());

    List<StatusGroupItem> groups = new ArrayList<>();
    groups.add(
        createGroup(
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.pending", null, "Pendentes", locale),
            pendingCount,
            "#f59e0b",
            "rgba(245, 158, 11, 0.1)"));
    groups.add(
        createGroup(
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.preparing", null, "Preparando", locale),
            preparingCount,
            "#3b82f6",
            "rgba(59, 130, 246, 0.1)"));
    groups.add(
        createGroup(
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.ready", null, "Prontos", locale),
            readyCount,
            "var(--green)",
            "var(--green-light)"));

    List<GroupedStatusItem> items = new ArrayList<>();
    ZonedDateTime now = Time.nowZonedDateTime();
    String currencySymbol = context.getRestaurant().getCurrency().getSymbol();

    for (int i = 0; i < Math.min(5, ongoingTickets.size()); i++) {
      TicketWithOrder wrapper = ongoingTickets.get(i);
      Ticket ticket = wrapper.ticket();
      Order order = wrapper.order();
      GroupedStatusItem item = new GroupedStatusItem();

      // Find order for table code/number
      String tableCode =
          messageSource.getMessage(
              "dashboard.widget.ongoing_orders.counter", null, "Balcão", locale);
      if (order.getTableId() != null) {
        String mesaFallback =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.table_fallback", null, "Mesa", locale);
        tableCode =
            tableRepository
                .findById(order.getTableId())
                .map(dev.thiagooliveira.tablesplit.domain.order.Table::getCod)
                .orElse(mesaFallback);
      }

      String shortId = ticket.getId().toString().substring(0, 8);
      String ticketTitle =
          messageSource.getMessage(
              "dashboard.widget.ongoing_orders.ticket_table",
              new Object[] {shortId, tableCode},
              String.format("Ticket #%s - Mesa %s", shortId, tableCode),
              locale);
      item.setTitle(ticketTitle);

      long minutes = Duration.between(ticket.getCreatedAt(), now).toMinutes();
      String timeStr;
      if (minutes <= 1) {
        timeStr =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.time.now", null, "agora mesmo", locale);
      } else {
        timeStr =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.time.minutes",
                new Object[] {minutes},
                String.format("há %d min", minutes),
                locale);
      }

      if (ticket.getStatus() == TicketStatus.READY) {
        String metaReady =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.meta.ready",
                new Object[] {ticket.getItems().size()},
                String.format("%d itens • Pronto para servir", ticket.getItems().size()),
                locale);
        item.setMeta(metaReady);
        item.setStatus("green");
      } else if (ticket.getStatus() == TicketStatus.PREPARING) {
        String metaPreparing =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.meta.preparing",
                new Object[] {ticket.getItems().size(), timeStr},
                String.format("%d itens • Preparando (%s)", ticket.getItems().size(), timeStr),
                locale);
        item.setMeta(metaPreparing);
        item.setStatus("blue");
      } else {
        String metaDefault =
            messageSource.getMessage(
                "dashboard.widget.ongoing_orders.meta.default",
                new Object[] {ticket.getItems().size(), timeStr},
                String.format("%d itens • %s", ticket.getItems().size(), timeStr),
                locale);
        item.setMeta(metaDefault);
        item.setStatus("amber");
      }

      var ticketPrice =
          ticket.getItems().stream()
              .map(
                  itemPrice ->
                      itemPrice
                          .getUnitPrice()
                          .multiply(java.math.BigDecimal.valueOf(itemPrice.getQuantity())))
              .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

      String formattedPrice =
          String.format("%s %,.2f", currencySymbol, ticketPrice.doubleValue())
              .replace(",", "X")
              .replace(".", ",")
              .replace("X", ".");
      item.setValue(formattedPrice);

      items.add(item);
    }

    GroupedStatusWidgetContent content = new GroupedStatusWidgetContent();
    content.setStatusGroups(groups);
    content.setItems(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("ongoing_orders");
    widget.setType(DashboardWidgetResponse.TypeEnum.GROUPED_STATUS);
    widget.setTitle(
        messageSource.getMessage(
            "dashboard.widget.ongoing_orders.title", null, "Pedidos em Andamento", locale));
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(6);
    widget.setGroupedStatusContent(content);

    return widget;
  }

  private StatusGroupItem createGroup(String label, int count, String color, String bg) {
    StatusGroupItem group = new StatusGroupItem();
    group.setLabel(label);
    group.setCount(count);
    group.setColor(color);
    group.setBg(bg);
    return group;
  }
}
