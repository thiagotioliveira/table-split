package dev.thiagooliveira.tablesplit.infrastructure.restaurant.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;

public class RestaurantTools extends AbstractTools {

  private final GetRestaurant getRestaurant;

  public RestaurantTools(
      GetRestaurant getRestaurant,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper,
      MessageSource messageSource) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper, messageSource);
    this.getRestaurant = getRestaurant;
  }

  @Tool("Get restaurant details and information")
  public String getRestaurantDetails() {
    return executeInTenantContext(
        "getRestaurantDetails",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          return getRestaurant
              .execute(restaurantId)
              .map(Object.class::cast)
              .orElse("Restaurant not found.");
        });
  }
}
