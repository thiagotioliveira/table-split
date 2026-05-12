package dev.thiagooliveira.tablesplit.infrastructure.order.service;

import dev.thiagooliveira.tablesplit.application.order.GetOrder;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.TableApiMapper;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.TablesResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetTablesOverview {

  private final GetTables getTables;
  private final GetOrder getOrder;
  private final TableApiMapper mapper;

  public GetTablesOverview(GetTables getTables, GetOrder getOrder, TableApiMapper mapper) {
    this.getTables = getTables;
    this.getOrder = getOrder;
    this.mapper = mapper;
  }

  public TablesResponse getTables(UUID restaurantId) {
    var result = getTables.execute(restaurantId);

    java.util.Map<UUID, java.math.BigDecimal> balances = new java.util.HashMap<>();
    result
        .tables()
        .forEach(
            t -> {
              var activeOrder = getOrder.execute(t.getId());
              var balance =
                  activeOrder
                      .map(
                          dev.thiagooliveira.tablesplit.domain.order.Order
                              ::calculateRemainingAmount)
                      .orElse(java.math.BigDecimal.ZERO);
              balances.put(t.getId(), balance);
            });

    return mapper.mapToTablesResponse(result, balances);
  }
}
