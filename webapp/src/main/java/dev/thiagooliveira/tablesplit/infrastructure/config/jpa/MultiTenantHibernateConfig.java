package dev.thiagooliveira.tablesplit.infrastructure.config.jpa;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure Hibernate 6 to use Spring-managed multi-tenancy beans. This resolves issues with
 * Hibernate attempting to instantiate classes manually.
 */
@Configuration
public class MultiTenantHibernateConfig {

  private static final Logger logger = LoggerFactory.getLogger(MultiTenantHibernateConfig.class);

  @Bean
  public HibernatePropertiesCustomizer multiTenantHibernatePropertiesCustomizer(
      MultiTenantConnectionProvider<?> multiTenantConnectionProvider,
      CurrentTenantIdentifierResolver<?> currentTenantIdentifierResolver) {

    return (hibernateProperties) -> {
      logger.debug("[MultiTenantHibernateConfig] Applying multi-tenancy settings to Hibernate...");
      // Use explicit strings to ensure compatibility across Hibernate versions
      hibernateProperties.put(
          "hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
      hibernateProperties.put(
          "hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
      hibernateProperties.put("hibernate.multiTenancy", "SCHEMA");
    };
  }
}
