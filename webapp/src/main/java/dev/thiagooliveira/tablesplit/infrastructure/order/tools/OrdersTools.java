package dev.thiagooliveira.tablesplit.infrastructure.order.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.order.GetTables;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.order.service.GetOrderOverview;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;

public class OrdersTools extends AbstractTools {

  private final GetOrderOverview getOrderOverview;
  private final GetTables getTables;

  public OrdersTools(
      GetOrderOverview getOrderOverview,
      GetTables getTables,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper,
      MessageSource messageSource) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper, messageSource);
    this.getOrderOverview = getOrderOverview;
    this.getTables = getTables;
  }

  @Tool("Get order overview for a specific table")
  public String getOrderOverview(
      @P("The unique identifier (UUID) or the code (cod) of the table") String tableId,
      @P("The language to use (PT or EN)") String languageCode) {
    return executeInTenantContext(
        "getOrderOverview",
        () -> {
          try {
            Language language = Language.valueOf(languageCode.toUpperCase());
            UUID id;
            try {
              id = UUID.fromString(tableId);
            } catch (IllegalArgumentException e) {
              // Not a UUID, try to find by code (supporting padding for single digits like "9" ->
              // "09")
              String formattedCode = tableId;
              if (tableId.matches("\\d{1,2}")) {
                formattedCode = String.format("%02d", Integer.parseInt(tableId));
              }

              UUID restaurantId = TenantContext.getRestaurantId();
              id =
                  getTables
                      .findByRestaurantIdAndCod(restaurantId, formattedCode)
                      .map(dev.thiagooliveira.tablesplit.domain.order.Table::getId)
                      .orElse(null);
            }

            if (id == null) {
              return getMessage("telegram.login.staff.not_found", language, tableId);
            }

            return getOrderOverview
                .getOrderByTable(id, language)
                .map(Object.class::cast)
                .orElse(getMessage("telegram.error.no_order", language));
          } catch (Exception e) {
            logger.error(
                "Error parsing tool arguments: tableId={}, languageCode={}",
                tableId,
                languageCode,
                e);
            return "Error: Invalid arguments. Please provide a valid UUID or table code for 'tableId' and 'PT' or 'EN' for 'languageCode'.";
          }
        });
  }
}
