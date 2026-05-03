package dev.thiagooliveira.tablesplit.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * IA Tools para relatórios e feedbacks. Gerencia as transações programaticamente e utiliza
 * ObjectMapper para evitar erros de serialização do GSON com classes nativas do Java (como
 * ZonedDateTime).
 */
public class ReportAndFeedbackTools {

  private static final Logger logger = LoggerFactory.getLogger(ReportAndFeedbackTools.class);

  private final GetReportsOverview getReportsOverview;
  private final GetFeedbackOverview getFeedbackOverview;
  private final GetFeedbackUnreadCount getFeedbackUnreadCount;
  private final TransactionTemplate transactionTemplate;
  private final ObjectMapper objectMapper;

  public ReportAndFeedbackTools(
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper) {
    this.getReportsOverview = getReportsOverview;
    this.getFeedbackOverview = getFeedbackOverview;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
    this.transactionTemplate = transactionTemplate;
    this.objectMapper = objectMapper;
  }

  @Tool(
      "Get a general overview of the restaurant revenue and sales stats for a specific number of days")
  public String getReportsOverview(
      @P("Number of days to look back (e.g., 1 for today/yesterday, 7 for last week)") int days) {
    return transactionTemplate.execute(
        status -> {
          try {
            logger.info("Tool getReportsOverview chamada via IA com days: {}", days);
            UUID restaurantId = getRestaurantIdFromContext();
            if (restaurantId == null) return "Erro: Restaurante não identificado.";
            Object result = getReportsOverview.execute(restaurantId, days);
            return objectMapper.writeValueAsString(result);
          } catch (Exception e) {
            logger.error("Erro ao processar getReportsOverview", e);
            return "Erro ao processar dados de faturamento: " + e.getMessage();
          }
        });
  }

  @Tool(
      "Get customer feedback overview, including ratings distribution and items needing attention")
  public String getFeedbackOverview(@P("Number of days to look back for feedback") int days) {
    return transactionTemplate.execute(
        status -> {
          try {
            logger.info("Tool getFeedbackOverview chamada via IA com days: {}", days);
            UUID restaurantId = getRestaurantIdFromContext();
            if (restaurantId == null) return "Erro: Restaurante não identificado.";
            ZonedDateTime since = ZonedDateTime.now().minusDays(days);
            Object result = getFeedbackOverview.execute(restaurantId, since);
            return objectMapper.writeValueAsString(result);
          } catch (Exception e) {
            logger.error("Erro ao processar getFeedbackOverview", e);
            return "Erro ao processar dados de feedback: " + e.getMessage();
          }
        });
  }

  @Tool("Get the count of unread customer feedbacks")
  public String getUnreadFeedbackCount() {
    return transactionTemplate.execute(
        status -> {
          try {
            logger.info("Tool getUnreadFeedbackCount chamada via IA");
            UUID restaurantId = getRestaurantIdFromContext();
            if (restaurantId == null) return "Erro: Restaurante não identificado.";
            long count = getFeedbackUnreadCount.execute(restaurantId);
            return String.valueOf(count);
          } catch (Exception e) {
            return "Erro ao contar feedbacks: " + e.getMessage();
          }
        });
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
