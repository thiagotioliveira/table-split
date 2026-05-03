package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import java.util.Arrays;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utility helper to handle database-specific SQL syntax differences between H2 (Local/Dev) and
 * PostgreSQL (Production).
 */
@Component
public class DatabaseDialectHelper {

  private final boolean h2;

  public DatabaseDialectHelper(Environment environment) {
    this.h2 = Arrays.asList(environment.getActiveProfiles()).contains("h2");
  }

  /** Returns true if the current active profile is H2. */
  public boolean isH2() {
    return h2;
  }

  /**
   * Generates the SQL command to set the schema search path (search_path in Postgres,
   * SCHEMA_SEARCH_PATH in H2).
   */
  public String getSetSchemaSql(String tenant) {
    if (tenant == null || tenant.trim().isEmpty() || "PUBLIC".equalsIgnoreCase(tenant)) {
      return h2 ? "SET SCHEMA_SEARCH_PATH PUBLIC" : "SET search_path TO PUBLIC";
    }
    return h2
        ? "SET SCHEMA_SEARCH_PATH " + tenant + ", PUBLIC"
        : "SET search_path TO \"" + tenant + "\", PUBLIC";
  }

  /**
   * Generates the SQL command to create a schema if it doesn't exist, handling identifier quoting.
   */
  public String getCreateSchemaSql(String tenant) {
    return h2
        ? "CREATE SCHEMA IF NOT EXISTS " + tenant
        : "CREATE SCHEMA IF NOT EXISTS \"" + tenant + "\"";
  }
}
