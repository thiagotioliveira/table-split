package dev.thiagooliveira.tablesplit.infrastructure.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ReportAndFeedbackTools {

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
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) return "Erro: Restaurante não identificado no contexto.";
    return getReportsOverview.execute(restaurantId, days);
  }

  @Tool(
      "Obtém o feedback dos clientes, incluindo distribuição de notas e itens que precisam de atenção")
  public Object getFeedbackOverview(int days) {
    UUID restaurantId = getRestaurantIdFromContext();
    if (restaurantId == null) return "Erro: Restaurante não identificado no contexto.";
    ZonedDateTime since = ZonedDateTime.now().minusDays(days);
    return getFeedbackOverview.execute(restaurantId, since);
  }

  @Tool("Obtém a quantidade de feedbacks que ainda não foram lidos pelo gestor")
  public long getUnreadFeedbackCount() {
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
