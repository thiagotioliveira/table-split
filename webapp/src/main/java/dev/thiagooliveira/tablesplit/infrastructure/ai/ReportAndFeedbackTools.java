package dev.thiagooliveira.tablesplit.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
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
  private final EntityManager entityManager;
  private final boolean isH2;

  public ReportAndFeedbackTools(
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      boolean isH2) {
    this.getReportsOverview = getReportsOverview;
    this.getFeedbackOverview = getFeedbackOverview;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
    this.transactionTemplate = transactionTemplate;
    this.objectMapper = objectMapper;
    this.entityManager = entityManager;
    this.isH2 = isH2;
  }

  private void setTenantSchema(String tenant) {
    if (tenant == null || tenant.trim().isEmpty() || "PUBLIC".equalsIgnoreCase(tenant)) {
      String sql = isH2 ? "SET SCHEMA_SEARCH_PATH PUBLIC" : "SET search_path TO PUBLIC";
      entityManager.createNativeQuery(sql).executeUpdate();
      return;
    }

    if (isH2) {
      // Syntax para H2 (Local/Testes)
      entityManager
          .createNativeQuery("SET SCHEMA_SEARCH_PATH " + tenant + ", PUBLIC")
          .executeUpdate();
    } else {
      // Syntax para PostgreSQL (Produção)
      entityManager
          .createNativeQuery("SET search_path TO \"" + tenant + "\", PUBLIC")
          .executeUpdate();
    }
  }

  @Tool(
      "Get a general overview of the restaurant revenue and sales stats for a specific number of days")
  public String getReportsOverview(
      @P("Number of days to look back (e.g., 1 for today/yesterday, 7 for last week)") int days) {
    return transactionTemplate.execute(
        status -> {
          try {
            String tenant = TenantContext.getCurrentTenant();
            logger.info("Tool getReportsOverview chamada via IA. TenantContext: {}", tenant);

            setTenantSchema(tenant);

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
            String tenant = TenantContext.getCurrentTenant();
            logger.info("Tool getFeedbackOverview chamada via IA. TenantContext: {}", tenant);

            setTenantSchema(tenant);

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
            String tenant = TenantContext.getCurrentTenant();
            logger.info("Tool getUnreadFeedbackCount chamada via IA. TenantContext: {}", tenant);

            setTenantSchema(tenant);

            UUID restaurantId = getRestaurantIdFromContext();
            if (restaurantId == null) return "Erro: Restaurante não identificado.";
            long count = getFeedbackUnreadCount.execute(restaurantId);
            return String.valueOf(count);
          } catch (Exception e) {
            return "Erro ao contar feedbacks: " + e.getMessage();
          }
        });
  }

  @Tool("Diagnose database schema and table existence for the current tenant")
  public String diagnoseDatabase() {
    return transactionTemplate.execute(
        status -> {
          try {
            String tenant = TenantContext.getCurrentTenant();
            if (tenant == null) return "Erro: TenantContext nulo.";

            setTenantSchema(tenant);

            StringBuilder report = new StringBuilder();
            report.append("Diagnóstico para o tenant: ").append(tenant).append("\n");

            // 1. Check Schema
            List<?> schemas =
                entityManager
                    .createNativeQuery(
                        "SELECT schema_name FROM information_schema.schemata WHERE schema_name = :tenant")
                    .setParameter("tenant", tenant)
                    .getResultList();

            if (schemas.isEmpty()) {
              report.append("❌ SCHEMA NÃO ENCONTRADO NO BANCO DE DADOS.\n");
            } else {
              report.append("✅ Schema encontrado.\n");

              // 2. Check Table
              List<?> tables =
                  entityManager
                      .createNativeQuery(
                          "SELECT table_name FROM information_schema.tables WHERE table_schema = :tenant AND table_name = 'orders'")
                      .setParameter("tenant", tenant)
                      .getResultList();

              if (tables.isEmpty()) {
                report.append("❌ TABELA 'orders' NÃO ENCONTRADA NESTE SCHEMA.\n");
              } else {
                report.append("✅ Tabela 'orders' encontrada.\n");
              }
            }

            return report.toString();
          } catch (Exception e) {
            return "Erro no diagnóstico: " + e.getMessage();
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
