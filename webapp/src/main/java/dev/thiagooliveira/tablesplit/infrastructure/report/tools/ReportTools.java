package dev.thiagooliveira.tablesplit.infrastructure.report.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.report.service.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.transaction.support.TransactionTemplate;

public class ReportTools extends AbstractTools {

  private final GetReportsOverview getReportsOverview;

  public ReportTools(
      GetReportsOverview getReportsOverview,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper);
    this.getReportsOverview = getReportsOverview;
  }

  @Tool(
      "Get a general overview of the restaurant revenue and sales stats for a specific number of days")
  public String getReportsOverview(
      @P("Number of days to look back (e.g., 1 for today/yesterday, 7 for last week)") int days) {
    return executeInTenantContext(
        "getReportsOverview",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getReportsOverview.execute(restaurantId, days);
        });
  }
}
