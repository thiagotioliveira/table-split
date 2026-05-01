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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

@Mapper(
    componentModel = "spring",
    imports = {
      java.util.UUID.class,
      dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.TicketResponse
          .StatusEnum.class
    })
public abstract class OrderApiMapper {

  @Autowired protected MessageSource messageSource;

  @Mapping(target = "totalRevenue", expression = "java(history.totalRevenue().doubleValue())")
  @Mapping(target = "avgTicket", expression = "java(history.avgTicket().doubleValue())")
  public abstract dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model
          .HistoryResponse
      mapToHistoryResponse(HistoryData history);

  @Mapping(target = "restaurantId", expression = "java(UUID.fromString(model.getRestaurantId()))")
  @Mapping(target = "id", expression = "java(UUID.fromString(model.getId()))")
  @Mapping(target = "status", expression = "java(StatusEnum.fromValue(model.getStatus().name()))")
  @Mapping(target = "createdAt", expression = "java(model.getCreatedAt().toOffsetDateTime())")
  @Mapping(target = "total", expression = "java(model.getTotal().doubleValue())")
  public abstract TicketResponse mapToTicketResponse(TicketModel model);

  @Mapping(target = "unitPrice", expression = "java(model.getUnitPrice().doubleValue())")
  @Mapping(target = "totalPrice", expression = "java(model.getTotalPrice().doubleValue())")
  @Mapping(target = "createdAt", expression = "java(model.getCreatedAt().toOffsetDateTime())")
  public abstract TicketItemResponse mapToTicketItemResponse(TicketItemModel model);

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
