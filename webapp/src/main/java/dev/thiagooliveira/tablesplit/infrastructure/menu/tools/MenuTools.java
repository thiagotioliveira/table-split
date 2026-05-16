package dev.thiagooliveira.tablesplit.infrastructure.menu.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetCombos;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.menu.GetPromotions;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;

public class MenuTools extends AbstractTools {

  private final GetCategory getCategory;
  private final GetItem getItem;
  private final GetPromotions getPromotions;
  private final GetCombos getCombos;

  public MenuTools(
      GetCategory getCategory,
      GetItem getItem,
      GetPromotions getPromotions,
      GetCombos getCombos,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper,
      MessageSource messageSource) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper, messageSource);
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.getPromotions = getPromotions;
    this.getCombos = getCombos;
  }

  @Tool("Get the full list of menu categories available in the restaurant")
  public String getCategories(
      @P("The language to use for category names (e.g., PT for Portuguese, EN for English)")
          Language language) {
    return executeInTenantContext(
        "getCategories",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getCategory.execute(restaurantId, List.of(language));
        });
  }

  @Tool("Get the detailed list of menu items, including names, descriptions, and prices")
  public String getItems(
      @P("The language to use for item names and descriptions (e.g., PT or EN)")
          Language language) {
    return executeInTenantContext(
        "getItems",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getItem.execute(restaurantId, List.of(language), true);
        });
  }

  @Tool("Get all active promotions and special offers currently available in the restaurant")
  public String getPromotions() {
    return executeInTenantContext(
        "getPromotions",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getPromotions.listByRestaurantId(restaurantId);
        });
  }

  @Tool("Get the list of available combos (bundled items) and their specific prices")
  public String getCombos() {
    return executeInTenantContext(
        "getCombos",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getCombos.listByRestaurantId(restaurantId);
        });
  }
}
