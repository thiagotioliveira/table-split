package dev.thiagooliveira.tablesplit.infrastructure.ai;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IA Tools para relatórios e feedbacks. Esta classe NÃO deve ter anotações de Proxy do Spring
 * (como @Transactional ou @Service) para garantir que o LangChain4j consiga ler as anotações @Tool
 * via reflexão.
 */
public class ReportAndFeedbackTools {

  private static final Logger logger = LoggerFactory.getLogger(ReportAndFeedbackTools.class);

  private final GetReportsOverview getReportsOverview;
  private final GetFeedbackOverview getFeedbackOverview;
  private final GetFeedbackUnreadCount getFeedbackUnreadCount;

  public ReportAndFeedbackTools(
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount) {
    this.getReportsOverview = getReportsOverview;
    this.getFeedbackOverview = getFeedbackOverview;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
  }

  @Tool(
      "Get a general overview of the restaurant revenue and sales stats for a specific number of days")
  public Object getReportsOverview(
      @P("Number of days to look back (e.g., 1 for today/yesterday, 7 for last week)") int days) {
    logger.info("Tool getReportsOverview chamada via IA com days: {}", days);
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) {
      logger.warn("restaurantId não identificado no contexto!");
      return "Erro: Restaurante não identificado no contexto.";
    }
    return getReportsOverview.execute(restaurantId, days);
  }

  @Tool(
      "Get customer feedback overview, including ratings distribution and items needing attention")
  public Object getFeedbackOverview(@P("Number of days to look back for feedback") int days) {
    logger.info("Tool getFeedbackOverview chamada via IA com days: {}", days);
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) {
      logger.warn("restaurantId não identificado no contexto!");
      return "Erro: Restaurante não identificado no contexto.";
    }
    ZonedDateTime since = ZonedDateTime.now().minusDays(days);
    return getFeedbackOverview.execute(restaurantId, since);
  }

  @Tool("Get the count of unread customer feedbacks")
  public long getUnreadFeedbackCount() {
    logger.info("Tool getUnreadFeedbackCount chamada via IA");
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) return -1;
    return getFeedbackUnreadCount.execute(restaurantId);
  }

  private UUID getRestaurantIdFromContext() {
    String tenant = TenantContext.getCurrentTenant();
    if (tenant == null || !tenant.startsWith("t_")) return null;
    try {
      return UUID.fromString(tenant.substring(2).replace("_", "-"));
    } catch (Exception e) {
      return null;
    }
  }
}
