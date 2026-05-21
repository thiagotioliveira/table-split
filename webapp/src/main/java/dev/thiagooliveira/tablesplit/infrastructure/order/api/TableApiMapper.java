package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.HistoryPaymentResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TableOrderHistoryResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TableResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TablesResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TicketItemResponse;
import dev.thiagooliveira.tablesplit.infrastructure.order.web.model.TicketItemModel;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public abstract class TableApiMapper {

  @org.springframework.beans.factory.annotation.Autowired
  protected org.springframework.context.MessageSource messageSource;

  @org.springframework.beans.factory.annotation.Autowired
  protected dev.thiagooliveira.tablesplit.domain.order.TableRepository tableRepository;

  protected String resolveCustomerName(
      Order order, java.util.UUID customerId, String tableCod, Language userLanguage) {

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

  public TableOrderHistoryResponse mapToOrderHistoryResponse(Order order, Language userLanguage) {
    if (order == null) return null;

    Map<String, String> customerNames =
        order.getCustomers().stream()
            .collect(
                Collectors.toMap(
                    c -> c.getId().toString(), OrderCustomer::getName, (v1, v2) -> v1));

    java.util.Locale locale =
        userLanguage != null
            ? java.util.Locale.forLanguageTag(userLanguage.getLabel())
            : org.springframework.context.i18n.LocaleContextHolder.getLocale();
    String tableLabel = messageSource.getMessage("customer.anonymous.table", null, "Mesa", locale);
    customerNames.put("null", tableLabel);

    String tableCod = null;
    if (order.getTableId() != null) {
      tableCod = tableRepository.findById(order.getTableId()).map(Table::getCod).orElse(null);
    }

    TableOrderHistoryResponse response = new TableOrderHistoryResponse();
    response.setId(order.getId().toString());
    response.setTableId(order.getTableId().toString());
    response.setTableCod(tableCod);
    response.setServiceFee(order.getServiceFee());
    response.setStatus(order.getStatus().name());
    response.setOpenedAt(order.getOpenedAt().toOffsetDateTime());
    if (order.getClosedAt() != null) {
      response.setClosedAt(order.getClosedAt().toOffsetDateTime());
    }

    final String finalTableCod = tableCod;
    response.setItems(
        order.getTickets().stream()
            .flatMap(
                t ->
                    t.getItems().stream()
                        .map(
                            item ->
                                mapToTicketItemResponse(
                                    TicketItemModel.fromDomain(
                                        item,
                                        resolveCustomerName(
                                            order,
                                            item.getCustomerId(),
                                            finalTableCod,
                                            userLanguage),
                                        t.getNote(),
                                        t.getCreatedAt(),
                                        userLanguage,
                                        resolveStatusLabel(item.getStatus(), userLanguage),
                                        item.getStatus().name().toLowerCase(),
                                        resolveCancellationReasonLabel(
                                            item.getCancellationReason(), userLanguage)))))
            .sorted(
                java.util.Comparator.comparing(
                        TicketItemResponse::getCreatedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                    .thenComparing(
                        TicketItemResponse::getName,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                    .thenComparing(
                        TicketItemResponse::getId,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
            .collect(Collectors.toList()));

    response.setPayments(
        order.getPayments().stream()
            .map(this::mapToHistoryPaymentResponse)
            .collect(Collectors.toList()));

    response.setCustomers(
        order.getCustomers().stream()
            .map(
                c ->
                    new dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model
                            .OrderCustomerResponse()
                        .id(c.getId())
                        .name(c.getName())
                        .total(order.calculateSubtotalByCustomer(c.getId()).doubleValue()))
            .collect(Collectors.toList()));

    response.setCustomerNames(customerNames);
    response.setTotal(order.calculateTotal().doubleValue());
    response.setSubtotal(order.calculateSubtotal().doubleValue());
    response.setServiceFeeAmount(order.feeApplied().doubleValue());

    return response;
  }

  @Mapping(target = "unitPrice", expression = "java(model.getUnitPrice().doubleValue())")
  @Mapping(target = "totalPrice", expression = "java(model.getTotalPrice().doubleValue())")
  @Mapping(target = "createdAt", expression = "java(model.getCreatedAt().toOffsetDateTime())")
  public abstract TicketItemResponse mapToTicketItemResponse(TicketItemModel model);

  @Mapping(target = "id", expression = "java(payment.getId().toString())")
  @Mapping(target = "amount", expression = "java(payment.getAmount().doubleValue())")
  @Mapping(target = "paidAt", expression = "java(payment.getPaidAt().toOffsetDateTime())")
  @Mapping(target = "method", expression = "java(payment.getMethod().name())")
  public abstract HistoryPaymentResponse mapToHistoryPaymentResponse(Payment payment);

  public TablesResponse mapToTablesResponse(
      GetTables.Result result, java.util.Map<java.util.UUID, java.math.BigDecimal> balances) {
    if (result == null) return null;
    TablesResponse response = new TablesResponse();
    response.setTables(
        result.tables().stream()
            .map(
                t ->
                    mapToTableResponse(
                        t,
                        balances.getOrDefault(t.getId(), java.math.BigDecimal.ZERO).doubleValue()))
            .collect(Collectors.toList()));
    response.setCount((int) result.count());
    response.setCountAvailable((int) result.countAvailable());
    response.setCountOccupied((int) result.countOccupied());
    response.setCountWaiting((int) result.countWaiting());
    return response;
  }

  @Mapping(target = "id", source = "table.id")
  @Mapping(target = "cod", source = "table.cod")
  @Mapping(target = "status", source = "table.status")
  @Mapping(target = "balance", source = "balance")
  public abstract TableResponse mapToTableResponse(Table table, Double balance);

  @ValueMapping(target = "WAITING_PAYMENT", source = "WAITING")
  public abstract TableResponse.StatusEnum mapStatus(
      dev.thiagooliveira.tablesplit.domain.order.TableStatus status);

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
