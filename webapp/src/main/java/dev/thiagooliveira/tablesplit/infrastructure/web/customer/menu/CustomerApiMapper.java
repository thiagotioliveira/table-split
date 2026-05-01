package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderCustomer;
import dev.thiagooliveira.tablesplit.domain.order.Payment;
import dev.thiagooliveira.tablesplit.domain.order.TicketItem;
import dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.OrderCustomerModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.PaymentModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.SimpleTicketItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.TableSummaryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.spec.v1.model.TicketItemCustomizationResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CustomerApiMapper {

  public List<SimpleTicketItem> mapToSimpleTicketItems(Order order, Language lang) {
    if (order == null) return List.of();
    return order.getTickets().stream()
        .flatMap(
            t ->
                t.getItems().stream()
                    .map(
                        item -> {
                          SimpleTicketItem dto =
                              mapToSimpleTicketItem(
                                  item, lang, order.getCustomerName(item.getCustomerId()));
                          dto.setCreatedAt(t.getCreatedAt().toOffsetDateTime());
                          return dto;
                        }))
        .collect(Collectors.toList());
  }

  @Mapping(target = "name", expression = "java(item.getName().get(lang))")
  @Mapping(target = "totalPrice", expression = "java(item.getTotalPrice().doubleValue())")
  @Mapping(target = "status", expression = "java(item.getStatus().name())")
  @Mapping(target = "statusLabel", expression = "java(item.getStatus().getLabel())")
  @Mapping(
      target = "customerId",
      expression = "java(item.getCustomerId() != null ? item.getCustomerId().toString() : null)")
  @Mapping(target = "customerName", source = "customerName")
  @Mapping(target = "customizations", source = "item.customizations")
  @Mapping(target = "rating", source = "item.rating")
  protected abstract SimpleTicketItem mapToSimpleTicketItem(
      TicketItem item, Language lang, String customerName);

  protected List<TicketItemCustomizationResponse> mapCustomizations(
      List<TicketItemCustomization> customizations) {
    if (customizations == null) return List.of();
    return customizations.stream()
        .map(
            c -> {
              TicketItemCustomizationResponse resp = new TicketItemCustomizationResponse();
              resp.setQuestion(c.title());
              resp.setOption(
                  c.options().stream()
                      .map(dev.thiagooliveira.tablesplit.domain.order.TicketItemOption::text)
                      .collect(Collectors.joining(", ")));
              return resp;
            })
        .collect(Collectors.toList());
  }

  public OrderCustomerModel mapToOrderCustomerModel(OrderCustomer customer, Order order) {
    OrderCustomerModel model = new OrderCustomerModel();
    model.setId(customer.getId());
    model.setName(customer.getName());
    model.setSubtotal(order.calculateSubtotalByCustomer(customer.getId()).doubleValue());
    return model;
  }

  public List<OrderCustomerModel> mapToOrderCustomerModels(Order order) {
    if (order == null) return List.of();
    return order.getCustomers().stream()
        .map(c -> mapToOrderCustomerModel(c, order))
        .collect(Collectors.toList());
  }

  @Mapping(target = "amount", expression = "java(payment.getAmount().doubleValue())")
  @Mapping(target = "paidAt", expression = "java(payment.getPaidAt().toOffsetDateTime())")
  @Mapping(target = "method", expression = "java(payment.getMethod().name())")
  public abstract PaymentModel mapToPaymentModel(Payment payment);

  public List<PaymentModel> mapToPaymentModels(Order order) {
    if (order == null) return List.of();
    return order.getPayments().stream().map(this::mapToPaymentModel).collect(Collectors.toList());
  }

  public TableSummaryModel mapToTableSummaryModel(Order order) {
    if (order == null) return null;
    TableSummaryModel model = new TableSummaryModel();
    model.setSubtotal(order.calculateSubtotal().doubleValue());
    model.setServiceFee(order.feeApplied().doubleValue());
    model.setTotal(order.calculateTotal().doubleValue());
    model.setPaidAmount(order.calculatePaidAmount().doubleValue());
    model.setRemainingAmount(order.calculateRemainingAmount().doubleValue());
    return model;
  }
}
