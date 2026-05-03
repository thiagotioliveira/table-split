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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * IA Tools para relatórios e feedbacks. Esta classe NÃO é um Bean do Spring para garantir que o
 * LangChain4j consiga ler as anotações @Tool sem interferência de Proxies. Gerenciamos as
 * transações programaticamente usando TransactionTemplate para evitar LazyInitializationException.
 */
public class ReportAndFeedbackTools {

  private static final Logger logger = LoggerFactory.getLogger(ReportAndFeedbackTools.class);

  private final GetReportsOverview getReportsOverview;
  private final GetFeedbackOverview getFeedbackOverview;
  private final GetFeedbackUnreadCount getFeedbackUnreadCount;
  private final TransactionTemplate transactionTemplate;

  public ReportAndFeedbackTools(
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      TransactionTemplate transactionTemplate) {
    this.getReportsOverview = getReportsOverview;
    this.getFeedbackOverview = getFeedbackOverview;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
    this.transactionTemplate = transactionTemplate;
  }

  @Tool(
      "Get a general overview of the restaurant revenue and sales stats for a specific number of days")
  public Object getReportsOverview(
      @P("Number of days to look back (e.g., 1 for today/yesterday, 7 for last week)") int days) {
    return transactionTemplate.execute(
        status -> {
          logger.info("Tool getReportsOverview chamada via IA com days: {}", days);
          UUID restaurantId = getRestaurantIdFromContext();
          if (restaurantId == null) {
            logger.warn("restaurantId não identificado no contexto!");
            return "Erro: Restaurante não identificado no contexto.";
          }
          return getReportsOverview.execute(restaurantId, days);
        });
  }

  @Tool(
      "Get customer feedback overview, including ratings distribution and items needing attention")
  public Object getFeedbackOverview(@P("Number of days to look back for feedback") int days) {
    return transactionTemplate.execute(
        status -> {
          logger.info("Tool getFeedbackOverview chamada via IA com days: {}", days);
          UUID restaurantId = getRestaurantIdFromContext();
          if (restaurantId == null) {
            logger.warn("restaurantId não identificado no contexto!");
            return "Erro: Restaurante não identificado no contexto.";
          }
          ZonedDateTime since = ZonedDateTime.now().minusDays(days);
          return getFeedbackOverview.execute(restaurantId, since);
        });
  }

  @Tool("Get the count of unread customer feedbacks")
  public long getUnreadFeedbackCount() {
    Long result =
        transactionTemplate.execute(
            status -> {
              logger.info("Tool getUnreadFeedbackCount chamada via IA");
              UUID restaurantId = getRestaurantIdFromContext();
              if (restaurantId == null) return -1L;
              return getFeedbackUnreadCount.execute(restaurantId);
            });
    return result != null ? result : -1L;
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
