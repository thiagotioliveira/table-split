package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.CloseTable;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.order.DeletePayment;
import dev.thiagooliveira.tablesplit.application.order.DeleteTable;
import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.OpenTable;
import dev.thiagooliveira.tablesplit.application.order.ProcessPayment;
import dev.thiagooliveira.tablesplit.domain.order.PaymentMethod;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.api.TablesApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.CreateTableRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.PaymentRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.TableOrderHistoryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
public class TableApiController implements TablesApi {

  private final TransactionalContext transactionalContext;
  private final CreateTable createTable;
  private final DeleteTable deleteTable;
  private final OpenTable openTable;
  private final CloseTable closeTable;
  private final GetOrder getOrder;
  private final ProcessPayment processPayment;
  private final DeletePayment deletePayment;
  private final TableApiMapper mapper;

  public TableApiController(
      TransactionalContext transactionalContext,
      CreateTable createTable,
      DeleteTable deleteTable,
      OpenTable openTable,
      CloseTable closeTable,
      GetOrder getOrder,
      ProcessPayment processPayment,
      DeletePayment deletePayment,
      TableApiMapper mapper) {
    this.transactionalContext = transactionalContext;
    this.createTable = createTable;
    this.deleteTable = deleteTable;
    this.openTable = openTable;
    this.closeTable = closeTable;
    this.getOrder = getOrder;
    this.processPayment = processPayment;
    this.deletePayment = deletePayment;
    this.mapper = mapper;
  }

  private AccountContext getContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (AccountContext) auth.getPrincipal();
  }

  @Override
  public ResponseEntity<Void> createTable(CreateTableRequest request) {
    var context = getContext();
    transactionalContext.execute(
        () ->
            createTable.execute(
                context.getId(), context.getRestaurant().getId(), request.getCod()));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteTable(UUID tableId) {
    transactionalContext.execute(() -> deleteTable.execute(tableId));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> openTable(UUID tableId) {
    var context = getContext();
    transactionalContext.execute(
        () -> openTable.execute(tableId, context.getRestaurant().getServiceFee(), null, null));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> closeOrder(UUID orderId) {
    transactionalContext.execute(() -> closeTable.execute(orderId));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<TableOrderHistoryResponse>> getTableHistory(
      UUID tableId, String status, java.time.OffsetDateTime start, java.time.OffsetDateTime end) {
    var context = getContext();
    dev.thiagooliveira.tablesplit.domain.order.OrderStatus orderStatus = null;
    if (status != null && !status.isEmpty()) {
      orderStatus = dev.thiagooliveira.tablesplit.domain.order.OrderStatus.valueOf(status);
    }

    java.time.ZonedDateTime startZ = start != null ? start.toZonedDateTime() : null;
    java.time.ZonedDateTime endZ = end != null ? end.toZonedDateTime() : null;

    var results =
        getOrder.findAllFiltered(tableId, orderStatus, startZ, endZ).stream()
            .map(o -> mapper.mapToOrderHistoryResponse(o, context.getUser().getLanguage()))
            .toList();
    return ResponseEntity.ok(results);
  }

  @Override
  public ResponseEntity<Void> processPayment(UUID tableId, PaymentRequest request) {
    transactionalContext.execute(
        () ->
            processPayment.execute(
                tableId,
                request.getCustomerId(),
                request.getAmount() != null
                    ? java.math.BigDecimal.valueOf(request.getAmount())
                    : java.math.BigDecimal.ZERO,
                request.getMethod() != null
                    ? PaymentMethod.valueOf(request.getMethod().name())
                    : PaymentMethod.CASH,
                request.getNote()));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deletePayment(UUID tableId, UUID paymentId) {
    transactionalContext.execute(() -> deletePayment.execute(tableId, paymentId));
    return ResponseEntity.ok().build();
  }
}
