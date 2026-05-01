package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model.TicketItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.table.spec.v1.model.HistoryPaymentResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.table.spec.v1.model.TableOrderHistoryResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.table.spec.v1.model.TicketItemResponse;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    response.setTickets(
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
                                        t.getCreatedAt(),
                                        userLanguage))))
            .collect(Collectors.toList()));

    response.setPayments(
        order.getPayments().stream()
            .map(this::mapToHistoryPaymentResponse)
            .collect(Collectors.toList()));

    response.setCustomerNames(customerNames);
    response.setTotal(order.calculateTotal().doubleValue());

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
}
