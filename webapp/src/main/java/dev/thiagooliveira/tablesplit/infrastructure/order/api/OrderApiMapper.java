package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Ticket;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketItemResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketModel;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
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
      dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketResponse.StatusEnum
          .class
    })
public abstract class OrderApiMapper {

  protected MessageSource messageSource;

  @Autowired
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Mapping(target = "restaurantId", expression = "java(UUID.fromString(model.getRestaurantId()))")
  @Mapping(target = "id", expression = "java(UUID.fromString(model.getId()))")
  @Mapping(target = "orderId", expression = "java(UUID.fromString(model.getOrderId()))")
  @Mapping(target = "status", expression = "java(StatusEnum.fromValue(model.getStatus().name()))")
  @Mapping(target = "createdAt", expression = "java(model.getCreatedAt().toOffsetDateTime())")
  @Mapping(target = "total", expression = "java(model.getTotal().doubleValue())")
  public abstract TicketResponse mapToTicketResponse(TicketModel model);

  @Mapping(target = "unitPrice", expression = "java(model.getUnitPrice().doubleValue())")
  @Mapping(target = "totalPrice", expression = "java(model.getTotalPrice().doubleValue())")
  @Mapping(target = "createdAt", expression = "java(model.getCreatedAt().toOffsetDateTime())")
  public abstract TicketItemResponse mapToTicketItemResponse(TicketItemModel model);

  public abstract dev.thiagooliveira.tablesplit.domain.order.CancellationReason mapReason(
      dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.CancelItemRequest
              .ReasonEnum
          reason);

  protected String resolveCustomerName(
      dev.thiagooliveira.tablesplit.domain.order.Order order,
      java.util.UUID customerId,
      String tableCod,
      Language userLanguage) {

    java.util.Optional<String> nameOpt = order.getCustomerName(customerId);
    if (nameOpt.isPresent()) {
      return nameOpt.get();
    }

    java.util.Locale locale =
        userLanguage != null
            ? java.util.Locale.forLanguageTag(userLanguage.getLabel())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();

    if (customerId == null) {
      if (order.getTableId() == null) {
        return messageSource.getMessage("customer.anonymous.takeaway", null, "Balcão", locale);
      } else {
        String tableLabel =
            messageSource.getMessage("customer.anonymous.table", null, "Mesa", locale);
        return tableCod != null ? tableLabel + " " + tableCod : tableLabel;
      }
    } else {
      return messageSource.getMessage("customer.anonymous.unknown", null, "Desconhecido", locale);
    }
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
                        resolveCustomerName(order, item.getCustomerId(), tableCod, userLanguage),
                        ticket.getNote(),
                        ticket.getCreatedAt(),
                        userLanguage,
                        resolveStatusLabel(item.getStatus(), userLanguage),
                        item.getStatus().name().toLowerCase(),
                        resolveCancellationReasonLabel(item.getCancellationReason(), userLanguage)))
            .sorted(
                java.util.Comparator.comparing(
                        TicketItemModel::getName,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                    .thenComparing(
                        item -> item.getId() != null ? item.getId().toString() : "",
                        java.util.Comparator.naturalOrder()))
            .toList();

    String customerName = itemModels.isEmpty() ? null : itemModels.get(0).getCustomerName();
    if (customerName == null || customerName.isBlank()) {
      customerName = resolveCustomerName(order, null, tableCod, userLanguage);
    }

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
        ticket.getStatus().name().toLowerCase(),
        resolveStatusLabel(ticket.getStatus(), userLanguage),
        ticket.getCreatedAt(),
        timeAgo,
        itemModels,
        ticket.calculateTotal(),
        urgent,
        ticket.getNote(),
        order.getId());
  }

  public dev.thiagooliveira.tablesplit.application.order.command.PlaceOrderCommand mapToCommand(
      java.util.UUID restaurantId,
      String tableCod,
      Integer defaultServiceFee,
      dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.PlaceOrderRequest
          request,
      java.util.UUID initiatedBy,
      Language language) {
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
        defaultServiceFee,
        request.getCustomers() != null
            ? request.getCustomers().stream()
                .map(
                    c ->
                        new dev.thiagooliveira.tablesplit.application.order.command.CustomerCommand(
                            c.getId(), c.getName()))
                .collect(Collectors.toList())
            : null,
        initiatedBy,
        language,
        request.getPaymentMethod() != null
            ? dev.thiagooliveira.tablesplit.domain.order.PaymentMethod.valueOf(
                request.getPaymentMethod().name())
            : null,
        request.getPaymentNote());
  }

  public dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.PlaceOrderResponse
      mapToPlaceOrderResponse(dev.thiagooliveira.tablesplit.domain.order.Order order) {
    List<dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketSummary>
        tickets =
            order.getTickets().stream()
                .map(
                    t ->
                        new dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model
                                .TicketSummary()
                            .id(t.getId())
                            .shortId(t.getId().toString().substring(0, 4).toUpperCase()))
                .toList();

    return new dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model
            .PlaceOrderResponse()
        .id(order.getId())
        .shortId(order.getId().toString().substring(0, 4).toUpperCase())
        .tickets(tickets);
  }

  protected String resolveStatusLabel(
      dev.thiagooliveira.tablesplit.domain.order.TicketStatus status, Language userLanguage) {
    if (status == null) return "";
    java.util.Locale locale =
        userLanguage != null
            ? java.util.Locale.forLanguageTag(userLanguage.getLabel())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();
    return messageSource.getMessage(
        "ticket.status." + status.name().toLowerCase(), null, status.name(), locale);
  }

  protected String resolveCancellationReasonLabel(
      dev.thiagooliveira.tablesplit.domain.order.CancellationReason reason, Language userLanguage) {
    if (reason == null) return null;
    java.util.Locale locale =
        userLanguage != null
            ? java.util.Locale.forLanguageTag(userLanguage.getLabel())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();
    return messageSource.getMessage(
        "cancellation.reason." + reason.name().toLowerCase(), null, reason.name(), locale);
  }
}
