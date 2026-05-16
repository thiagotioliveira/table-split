package dev.thiagooliveira.tablesplit.infrastructure.account.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.thiagooliveira.tablesplit.application.account.GetStaff;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.tools.AbstractTools;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;

public class StaffTools extends AbstractTools {

  private final GetStaff getStaff;

  public StaffTools(
      GetStaff getStaff,
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper,
      MessageSource messageSource) {
    super(transactionTemplate, objectMapper, entityManager, dialectHelper, messageSource);
    this.getStaff = getStaff;
  }

  @Tool("Get list of staff members and employees")
  public String getStaffList() {
    return executeInTenantContext(
        "getStaffList",
        () -> {
          UUID restaurantId = TenantContext.getRestaurantId();
          var staff = getStaff.list(restaurantId);
          if (staff.isEmpty()) {
            return "No staff members registered for this restaurant.";
          }
          return staff;
        });
  }
}
