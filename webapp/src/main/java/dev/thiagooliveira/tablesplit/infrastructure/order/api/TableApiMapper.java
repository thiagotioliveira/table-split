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

  public TableOrderHistoryResponse mapToOrderHistoryResponse(Order order, Language userLanguage) {
    if (order == null) return null;

    Map<String, String> customerNames =
        order.getCustomers().stream()
            .collect(
                Collectors.toMap(
                    c -> c.getId().toString(), OrderCustomer::getName, (v1, v2) -> v1));
    customerNames.put("null", "Mesa");

    TableOrderHistoryResponse response = new TableOrderHistoryResponse();
    response.setId(order.getId().toString());
    response.setTableId(order.getTableId().toString());
    response.setServiceFee(order.getServiceFee());
    response.setStatus(order.getStatus().name());
    response.setOpenedAt(order.getOpenedAt().toOffsetDateTime());
    if (order.getClosedAt() != null) {
      response.setClosedAt(order.getClosedAt().toOffsetDateTime());
    }

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
                                        order.getCustomerName(item.getCustomerId()),
                                        t.getNote(),
                                        t.getCreatedAt(),
                                        userLanguage))))
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
}
