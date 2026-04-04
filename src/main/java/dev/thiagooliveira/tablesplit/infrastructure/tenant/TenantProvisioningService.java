package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for provisioning new tenant schemas. It creates the schema and runs the
 * tenant-specific Liquibase changelogs.
 */
@Service
public class TenantProvisioningService {

  private static final Logger logger = LoggerFactory.getLogger(TenantProvisioningService.class);

  private final DataSource dataSource;
  private static final String TENANT_CHANGELOG = "db/changelog/db.changelog-tenant-master.yaml";

  public TenantProvisioningService(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void provisionTenant(UUID restaurantId) {
    String tenantId = TenantContext.generateTenantIdentifier(restaurantId);

    try (Connection connection = dataSource.getConnection()) {
      // 1. Create Schema
      try (Statement statement = connection.createStatement()) {
        String dbName = connection.getMetaData().getDatabaseProductName();
        String sql =
            "H2".equalsIgnoreCase(dbName)
                ? "CREATE SCHEMA IF NOT EXISTS " + tenantId
                : "CREATE SCHEMA IF NOT EXISTS \"" + tenantId + "\"";
        statement.execute(sql);
      }

      // 2. Run Liquibase on the new schema
      runLiquibase(connection, tenantId);

      // 3. Commit only if NOT in a Spring transaction (to avoid committing test transactions)
      if (!org.springframework.transaction.support.TransactionSynchronizationManager
              .isActualTransactionActive()
          && !connection.getAutoCommit()) {
        connection.commit();
      }

    } catch (SQLException | liquibase.exception.LiquibaseException e) {
      throw new RuntimeException(
          "Failed to provision tenant schema for restaurant: " + restaurantId, e);
    }
  }

  private void runLiquibase(Connection connection, String schemaName)
      throws LiquibaseException, SQLException {
    // Set search path for the session to the new schema
    String dbName = connection.getMetaData().getDatabaseProductName();
    String sql =
        "H2".equalsIgnoreCase(dbName)
            ? "SET SCHEMA_SEARCH_PATH " + schemaName + ", PUBLIC"
            : "SET search_path TO \"" + schemaName + "\", PUBLIC";

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }

    JdbcConnection jdbcConnection = new JdbcConnection(connection);
    Database database =
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
    database.setDefaultSchemaName(schemaName);
    database.setLiquibaseSchemaName(schemaName);
    database.setDefaultCatalogName(null);
    database.setLiquibaseCatalogName(null);

    Liquibase liquibase =
        new Liquibase(TENANT_CHANGELOG, new ClassLoaderResourceAccessor(), database);
    liquibase.update("");
    logger.debug(
        "[TenantProvisioning] Liquibase update successful for tenant schema: {}", schemaName);
  }
}
