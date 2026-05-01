package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.HistoryData;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.TicketItemResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.TicketResponse;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class OrderApiMapper {

  private final MessageSource messageSource;

  public OrderApiMapper(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model
          .HistoryResponse
      mapToHistoryResponse(HistoryData history) {
    return new dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model
            .HistoryResponse()
        .orders(
            history.orders().stream().map(this::mapToTicketResponse).collect(Collectors.toList()))
        .totalOrders(history.totalOrders())
        .totalRevenue(history.totalRevenue().doubleValue())
        .avgTicket(history.avgTicket().doubleValue());
  }

  public TicketResponse mapToTicketResponse(TicketModel model) {
    return new TicketResponse()
        .restaurantId(java.util.UUID.fromString(model.getRestaurantId()))
        .id(java.util.UUID.fromString(model.getId()))
        .shortId(model.getShortId())
        .tableCod(model.getTableCod())
        .customerName(model.getCustomerName())
        .status(TicketResponse.StatusEnum.fromValue(model.getStatus().name()))
        .statusClass(model.getStatusClass())
        .statusLabel(model.getStatusLabel())
        .createdAt(model.getCreatedAt().toOffsetDateTime())
        .timeAgo(model.getTimeAgo())
        .items(
            model.getItems().stream()
                .map(this::mapToTicketItemResponse)
                .collect(Collectors.toList()))
        .total(model.getTotal().doubleValue())
        .urgent(model.isUrgent())
        .note(model.getNote());
  }

  public TicketItemResponse mapToTicketItemResponse(TicketItemModel model) {
    return new TicketItemResponse()
        .id(model.getId())
        .customerId(model.getCustomerId())
        .customerName(model.getCustomerName())
        .name(model.getName())
        .names(model.getNames())
        .quantity(model.getQuantity())
        .unitPrice(model.getUnitPrice().doubleValue())
        .totalPrice(model.getTotalPrice().doubleValue())
        .note(model.getNote())
        .status(model.getStatus())
        .statusClass(model.getStatusClass())
        .rating(model.getRating())
        .createdAt(model.getCreatedAt().toOffsetDateTime())
        .customizationSummary(model.getCustomizationSummary());
  }

  public TicketModel mapToModel(
      Ticket ticket,
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      String tableCod,
      Language userLanguage) {
    List<TicketItemModel> itemModels =
        ticket.getItems().stream()
            .map(
                item ->
                    TicketItemModel.fromDomain(
                        item,
                        order.getCustomerName(item.getCustomerId()),
                        ticket.getCreatedAt(),
                        userLanguage))
            .toList();

    String customerName = itemModels.isEmpty() ? "Cliente" : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) customerName = "Mesa " + tableCod;

    String timeAgo =
        dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.timeAgo(
            ticket.getCreatedAt(), messageSource, userLanguage);
    long minutes = Duration.between(ticket.getCreatedAt(), Time.nowZonedDateTime()).toMinutes();
    boolean urgent = minutes > 15 && ticket.getStatus().isPending();

    return new TicketModel(
        order.getRestaurantId(),
        ticket.getId(),
        tableCod,
        customerName,
        ticket.getStatus(),
        ticket.getCreatedAt(),
        timeAgo,
        itemModels,
        ticket.calculateTotal(),
        urgent,
        ticket.getNote());
  }

  public dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand mapToCommand(
      java.util.UUID restaurantId,
      String tableCod,
      Integer defaultServiceFee,
      dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.PlaceOrderRequest
          request) {
    return new dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand(
        restaurantId,
        tableCod,
        request.getTickets().stream()
            .map(
                t ->
                    new dev.thiagooliveira.tablesplit.application.order.command.TicketCommand(
                        t.getNote(),
                        t.getItems().stream()
                            .map(
                                i ->
                                    new dev.thiagooliveira.tablesplit.application.order.command
                                        .TicketItemCommand(
                                        i.getItemId(),
                                        i.getCustomerId(),
                                        i.getQuantity(),
                                        i.getNote(),
                                        i.getPromotionId(),
                                        i.getOriginalPrice() != null
                                            ? java.math.BigDecimal.valueOf(i.getOriginalPrice())
                                            : null,
                                        i.getDiscountType(),
                                        i.getDiscountValue() != null
                                            ? java.math.BigDecimal.valueOf(i.getDiscountValue())
                                            : null,
                                        i.getCustomizations() != null
                                            ? i.getCustomizations().stream()
                                                .map(
                                                    c ->
                                                        new dev.thiagooliveira.tablesplit
                                                            .application.order.command
                                                            .TicketItemCustomizationCommand(
                                                            c.getTitle(),
                                                            c.getOptions().stream()
                                                                .map(
                                                                    o ->
                                                                        new dev.thiagooliveira
                                                                            .tablesplit.application
                                                                            .order.command
                                                                            .TicketItemOptionCommand(
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
        request.getServiceFee() != null ? request.getServiceFee() : defaultServiceFee,
        request.getCustomers() != null
            ? request.getCustomers().stream()
                .map(
                    c ->
                        new dev.thiagooliveira.tablesplit.application.order.command.CustomerCommand(
                            c.getId(), c.getName()))
                .collect(Collectors.toList())
            : null);
  }
}
