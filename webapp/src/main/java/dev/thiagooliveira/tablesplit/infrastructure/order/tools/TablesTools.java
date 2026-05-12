package dev.thiagooliveira.tablesplit.infrastructure.order.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.order.service.GetTablesOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.transaction.support.TransactionTemplate;

public class TablesTools extends AbstractTools {

  private final GetTablesOverview getTablesOverview;

  public TablesTools(
      GetTablesOverview getTablesOverview,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper);
    this.getTablesOverview = getTablesOverview;
  }

  @Tool("Get tables overview, including balance and status")
  public String getTablesOverview() {
    return executeInTenantContext(
        "getFeedbackOverview",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getTablesOverview.getTables(restaurantId);
        });
  }
}
