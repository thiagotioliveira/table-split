package dev.thiagooliveira.tablesplit.infrastructure.order.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import jakarta.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;

public class FeedbackTools extends AbstractTools {

  private final GetFeedbackOverview getFeedbackOverview;
  private final GetFeedbackUnreadCount getFeedbackUnreadCount;

  public FeedbackTools(
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper,
      MessageSource messageSource) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper, messageSource);
    this.getFeedbackOverview = getFeedbackOverview;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
  }

  @Tool(
      "Get customer feedback overview, including ratings distribution and items needing attention")
  public String getFeedbackOverview(@P("Number of days to look back for feedback") int days) {
    return executeInTenantContext(
        "getFeedbackOverview",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          ZonedDateTime since = Time.nowZonedDateTime().minusDays(days);
          return getFeedbackOverview.execute(restaurantId, since);
        });
  }

  @Tool("Get the count of unread customer feedbacks")
  public String getUnreadFeedbackCount() {
    return executeInTenantContext(
        "getUnreadFeedbackCount",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getFeedbackUnreadCount.execute(restaurantId);
        });
  }
}
