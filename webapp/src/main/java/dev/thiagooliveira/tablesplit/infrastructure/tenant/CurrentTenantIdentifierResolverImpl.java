package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Hibernate resolver that returns the current tenant from {@link TenantContext}. This is used to
 * route requests to the correct schema.
 */
@Component("currentTenantIdentifierResolver")
public class CurrentTenantIdentifierResolverImpl
    implements CurrentTenantIdentifierResolver<String> {

  private static final Logger logger =
      LoggerFactory.getLogger(CurrentTenantIdentifierResolverImpl.class);

  public static final String DEFAULT_TENANT = "PUBLIC";

  @Override
  public String resolveCurrentTenantIdentifier() {
    String tenant = TenantContext.getCurrentTenant();
    String resolved = tenant != null ? tenant : DEFAULT_TENANT;
    logger.info("[TenantResolver] Resolved tenant: {}", resolved);
    return resolved;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }
}
