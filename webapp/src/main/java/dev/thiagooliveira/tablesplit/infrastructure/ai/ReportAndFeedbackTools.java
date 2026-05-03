package dev.thiagooliveira.tablesplit.infrastructure.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
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
      "Obtém um resumo geral do faturamento e vendas do restaurante para um determinado número de dias")
  public Object getReportsOverview(int days) {
    logger.info("Tool getReportsOverview chamada com days: {}", days);
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) {
      logger.warn("restaurantId não identificado no contexto!");
      return "Erro: Restaurante não identificado no contexto.";
    }
    return getReportsOverview.execute(restaurantId, days);
  }

  @Tool(
      "Obtém o feedback dos clientes, incluindo distribuição de notas e itens que precisam de atenção")
  public Object getFeedbackOverview(int days) {
    logger.info("Tool getFeedbackOverview chamada com days: {}", days);
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) {
      logger.warn("restaurantId não identificado no contexto!");
      return "Erro: Restaurante não identificado no contexto.";
    }
    ZonedDateTime since = Time.nowZonedDateTime().minusDays(days);
    return getFeedbackOverview.execute(restaurantId, since);
  }

  @Tool("Obtém a quantidade de feedbacks que ainda não foram lidos pelo gestor")
  public long getUnreadFeedbackCount() {
    logger.info("Tool getUnreadFeedbackCount chamada");
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) {
      logger.warn("restaurantId não identificado no contexto!");
      return -1;
    }
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
