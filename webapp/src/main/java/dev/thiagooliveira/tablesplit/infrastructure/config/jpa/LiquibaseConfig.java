package dev.thiagooliveira.tablesplit.infrastructure.config.jpa;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RESTORED: Manual configuration for Liquibase to ensure it runs correctly even when multi-tenancy
 * is active in Hibernate.
 */
@Configuration
public class LiquibaseConfig {

  private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfig.class);

  private final DataSource dataSource;

  public LiquibaseConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Bean
  public SpringLiquibase liquibase() {
    logger.debug("RESTORED: Manual Liquibase initialization starting...");
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
    liquibase.setDefaultSchema("PUBLIC");
    liquibase.setDropFirst(false);
    liquibase.setShouldRun(true);
    return liquibase;
  }
}
