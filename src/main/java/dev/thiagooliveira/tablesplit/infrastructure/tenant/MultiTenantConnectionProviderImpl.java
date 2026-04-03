package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

/** Provides database connections and sets the schema (search_path) according to the tenant. */
@Component("multiTenantConnectionProvider")
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

  private final DataSource dataSource;

  public MultiTenantConnectionProviderImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Connection getAnyConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    connection.close();
  }

  @Override
  public Connection getConnection(String tenantIdentifier) throws SQLException {
    Connection connection = getAnyConnection();
    try {
      setSearchPath(connection, tenantIdentifier);
    } catch (SQLException e) {
      connection.close();
      throw e;
    }
    return connection;
  }

  private void setSearchPath(Connection connection, String tenantIdentifier) throws SQLException {
    String dbName = connection.getMetaData().getDatabaseProductName();
    String sql;

    // Safety check for null or empty tenant
    if (tenantIdentifier == null
        || tenantIdentifier.trim().isEmpty()
        || "PUBLIC".equalsIgnoreCase(tenantIdentifier)) {
      tenantIdentifier = "PUBLIC";
      if ("H2".equalsIgnoreCase(dbName)) {
        sql = "SET SCHEMA_SEARCH_PATH PUBLIC";
      } else {
        sql = "SET search_path TO PUBLIC";
      }
    } else {
      if ("H2".equalsIgnoreCase(dbName)) {
        sql = "SET SCHEMA_SEARCH_PATH " + tenantIdentifier + ", PUBLIC";
      } else {
        sql = "SET search_path TO " + tenantIdentifier + ", PUBLIC";
      }
    }

    System.out.println(
        "[MultiTenantConnection] Setting schema to: " + tenantIdentifier + " (SQL: " + sql + ")");
    connection.createStatement().execute(sql);
  }

  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection)
      throws SQLException {
    try {
      String dbName = connection.getMetaData().getDatabaseProductName();
      String sql =
          "H2".equalsIgnoreCase(dbName)
              ? "SET SCHEMA_SEARCH_PATH PUBLIC"
              : "SET search_path TO PUBLIC";
      connection.createStatement().execute(sql);
    } catch (SQLException e) {
      // ignore
    }
    connection.close();
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @Override
  public boolean isUnwrappableAs(Class<?> unwrapType) {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> unwrapType) {
    return null;
  }
}
