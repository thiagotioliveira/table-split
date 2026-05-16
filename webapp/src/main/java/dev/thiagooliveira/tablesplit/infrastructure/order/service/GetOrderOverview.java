package dev.thiagooliveira.tablesplit.infrastructure.order.service;

import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.TableApiMapper;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TableOrderHistoryResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetOrderOverview {
  private final GetOrder getOrder;
  private final TableApiMapper mapper;

  public GetOrderOverview(GetOrder getOrder, TableApiMapper mapper) {
    this.getOrder = getOrder;
    this.mapper = mapper;
  }

  public java.util.Optional<TableOrderHistoryResponse> getOrderByTable(
      UUID tableId, Language language) {
    return getOrder.execute(tableId).map(o -> mapper.mapToOrderHistoryResponse(o, language));
  }
}
