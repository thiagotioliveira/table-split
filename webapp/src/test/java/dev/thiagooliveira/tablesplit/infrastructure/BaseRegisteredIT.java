package dev.thiagooliveira.tablesplit.infrastructure;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.account.PendingRegistrationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseRegisteredIT extends BaseIT {

  protected static final String REGISTERED_EMAIL = "authenticated.it@example.com";

  @Autowired private PendingRegistrationRepository pendingRegistrationRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Override
  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();

    // Clean up database tables dynamically to avoid dirty state and FK failures across tests
    try {
      jdbcTemplate.execute(
          (Connection conn) -> {
            boolean originalAutoCommit = conn.getAutoCommit();
            try (Statement stmt = conn.createStatement()) {
              if (!originalAutoCommit) {
                conn.setAutoCommit(true);
              }

              String dbProduct = conn.getMetaData().getDatabaseProductName();
              boolean isH2 = dbProduct != null && dbProduct.toLowerCase().contains("h2");

              // 1. Get all user tables dynamically
              List<String> tables = new ArrayList<>();
              DatabaseMetaData metaData = conn.getMetaData();
              try (ResultSet rs = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
                while (rs.next()) {
                  String schema = rs.getString("TABLE_SCHEM");
                  String tableName = rs.getString("TABLE_NAME");
                  if (schema != null
                      && (schema.equalsIgnoreCase("public")
                          || schema.toUpperCase().startsWith("T_"))) {
                    // Avoid truncating Liquibase tables
                    if (!tableName.equalsIgnoreCase("databasechangelog")
                        && !tableName.equalsIgnoreCase("databasechangeloglock")) {
                      tables.add(schema + "." + tableName);
                    }
                  }
                }
              }

              // 2. Temporarily disable referential integrity/constraints
              if (isH2) {
                stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
              } else {
                stmt.execute("SET CONSTRAINTS ALL DEFERRED");
              }

              // 3. Truncate user tables
              for (String table : tables) {
                try {
                  if (isH2) {
                    stmt.execute("TRUNCATE TABLE " + table);
                  } else {
                    stmt.execute("TRUNCATE TABLE " + table + " CASCADE");
                  }
                } catch (Exception e) {
                  // Fallback to DELETE
                  try {
                    stmt.execute("DELETE FROM " + table);
                  } catch (Exception ex) {
                    // Ignore
                  }
                }
              }

              // 4. Restore constraints
              if (isH2) {
                stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
              }
            } finally {
              if (!originalAutoCommit) {
                conn.setAutoCommit(false);
              }
            }
            return null;
          });
    } catch (Exception e) {
      // Ignore
    }

    // Register a user first to have a valid context
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("plan", Plan.PROFESSIONAL.name())
                .param("user.firstName", "Thiago")
                .param("user.lastName", "Oliveira")
                .param("user.email", REGISTERED_EMAIL)
                .param("user.phone", "123456789")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Authenticated Restaurant")
                .param("restaurant.slug", "authenticated-restaurant")
                .param("restaurant.description", "Descricao Authenticated")
                .param("restaurant.phone", "987654321")
                .param("restaurant.email", "contato@authenticated-it.com")
                .param("restaurant.website", "www.authenticated-it.com")
                .param("restaurant.address", "Rua Authenticated, 123")
                .param("restaurant.currency", Currency.BRL.name())
                .param("restaurant.serviceFee", "10")
                .param("restaurant.cuisineType", CuisineType.BRAZILIAN.name())
                .param("restaurant.averagePrice", AveragePrice.PRICE_20_50.name())
                .param("restaurant.tags", RestaurantTag.WIFI.name(), RestaurantTag.PARKING.name())
                .param("restaurant.numberOfTables", "10"))
        .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());

    // Verify the pending registration so the user is created in the database
    var pending =
        pendingRegistrationRepository
            .findByEmail(REGISTERED_EMAIL)
            .orElseThrow(
                () -> new IllegalStateException("Pending registration not found for test email"));

    mockMvc
        .perform(
            post("/register/verify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", REGISTERED_EMAIL)
                .param("code", pending.getCode()))
        .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());
  }
}
