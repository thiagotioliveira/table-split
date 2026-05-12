package dev.thiagooliveira.tablesplit.infrastructure.claudio.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractTools {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected final TransactionTemplate transactionTemplate;
  protected final ObjectMapper objectMapper;
  protected final EntityManager entityManager;
  protected final DatabaseDialectHelper dialectHelper;

  protected AbstractTools(
      TransactionTemplate transactionTemplate,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper) {
    this.transactionTemplate = transactionTemplate;
    this.objectMapper = objectMapper;
    this.entityManager = entityManager;
    this.dialectHelper = dialectHelper;
  }

  protected void setTenantSchema(String tenant) {
    String sql = dialectHelper.getSetSchemaSql(tenant);
    entityManager.createNativeQuery(sql).executeUpdate();
  }

  protected String executeInTenantContext(String toolName, Supplier<Object> action) {
    return transactionTemplate.execute(
        status -> {
          try {
            String tenant = TenantContext.getCurrentTenant();
            logger.debug("Tool {} called via AI. TenantContext: {}", toolName, tenant);

            setTenantSchema(tenant);

            UUID restaurantId = TenantContext.getRestaurantId();
            if (restaurantId == null) {
              return "Error: Restaurant not identified.";
            }

            Object result = action.get();
            if (result instanceof String) {
              return (String) result;
            }
            return objectMapper.writeValueAsString(result);
          } catch (Exception e) {
            logger.error("Error processing " + toolName, e);
            return "Error processing " + toolName + ": " + e.getMessage();
          }
        });
  }
}
