package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Provides database connections and sets the schema (search_path) according to the tenant. */
@Component("multiTenantConnectionProvider")
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

  private static final Logger logger =
      LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);

  private final DataSource dataSource;
  private final DatabaseDialectHelper dialectHelper;

  public MultiTenantConnectionProviderImpl(
      DataSource dataSource, DatabaseDialectHelper dialectHelper) {
    this.dataSource = dataSource;
    this.dialectHelper = dialectHelper;
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
    String sql = dialectHelper.getSetSchemaSql(tenantIdentifier);
    logger.debug("[MultiTenantConnection] Setting schema to: {} (SQL: {})", tenantIdentifier, sql);
    connection.createStatement().execute(sql);
  }

  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection)
      throws SQLException {
    try {
      String sql = dialectHelper.getSetSchemaSql("PUBLIC");
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
