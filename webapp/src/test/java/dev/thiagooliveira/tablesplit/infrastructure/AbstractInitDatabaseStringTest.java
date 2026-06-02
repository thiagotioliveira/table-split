package dev.thiagooliveira.tablesplit.infrastructure;

import dev.thiagooliveira.tablesplit.domain.account.AccountStatus;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import dev.thiagooliveira.tablesplit.infrastructure.account.persistence.AccountEntity;
import dev.thiagooliveira.tablesplit.infrastructure.account.persistence.UserEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.LocalizedTextEntity;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.TableEntity;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantImageEntity;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantProvisioningService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractInitDatabaseStringTest extends AbstractMockMvcSpringTest {

  static PostgreSQLContainer<?> postgres;

  @BeforeAll
  static void beforeAllPersistentDbCheck(org.junit.jupiter.api.TestInfo testInfo) {
    boolean isPersistent =
        testInfo
            .getTestClass()
            .map(
                clazz ->
                    org.springframework.core.annotation.AnnotationUtils.findAnnotation(
                            clazz, PersistentDatabaseActivated.class)
                        != null)
            .orElse(false);

    if (isPersistent) {
      if (postgres == null) {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
      }
      if (!postgres.isRunning()) {
        postgres.start();
      }
    }
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    if (postgres != null && postgres.isRunning()) {
      registry.add("spring.datasource.url", postgres::getJdbcUrl);
      registry.add("spring.datasource.username", postgres::getUsername);
      registry.add("spring.datasource.password", postgres::getPassword);
    }
  }

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private EntityManager entityManager;

  @Autowired private TransactionTemplate transactionTemplate;

  @Autowired private TenantProvisioningService tenantProvisioningService;

  @Autowired private PasswordEncoder passwordEncoder;

  protected AccountData starterAccount;
  protected AccountData professionalAccount;
  protected boolean isH2;

  @Override
  @BeforeEach
  protected void setUp() {
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
              isH2 = dbProduct != null && dbProduct.toLowerCase().contains("h2");

              // 1. Get all user tables dynamically
              List<String> tables = new ArrayList<>();
              DatabaseMetaData metaData = conn.getMetaData();
              try (ResultSet rs = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
                while (rs.next()) {
                  String schema = rs.getString("TABLE_SCHEM");
                  String tableName = rs.getString("TABLE_NAME");
                  if (schema != null
                      && (schema.equalsIgnoreCase("public")
                          || schema.toUpperCase().startsWith("T_"))
                      && !tableName.equalsIgnoreCase("databasechangelog")
                      && !tableName.equalsIgnoreCase("databasechangeloglock")) {
                    tables.add(schema + "." + tableName);
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

    starterAccount = createTenantEnvironment("Starter", Plan.STARTER);

    professionalAccount = createTenantEnvironment("Pro", Plan.PROFESSIONAL);
  }

  private AccountData createTenantEnvironment(String prefix, Plan plan) {

    AccountEntity account = new AccountEntity();
    UserEntity user = new UserEntity();
    RestaurantEntity restaurant = new RestaurantEntity();

    transactionTemplate.executeWithoutResult(
        status -> {
          account.setId(UUID.randomUUID());
          account.setCreatedAt(OffsetDateTime.now());
          account.setPlan(plan);
          account.setStatus(AccountStatus.ACTIVE);
          entityManager.persist(account);

          user.setId(UUID.randomUUID());
          user.setAccountId(account.getId());
          user.setFirstName(prefix);
          user.setLastName("User");
          user.setEmail(prefix.toLowerCase() + "@demo.com");
          user.setPassword(passwordEncoder.encode("pass"));
          user.setLanguage(Language.PT);
          user.setRole(Role.RESTAURANT_ADMIN);
          entityManager.persist(user);

          restaurant.setId(UUID.randomUUID());
          restaurant.setAccountId(account.getId());
          restaurant.setName(prefix + " Restaurant");
          restaurant.setDescription(prefix + " Description");
          restaurant.setSlug(prefix.toLowerCase() + "-restaurant");
          restaurant.setEmail(prefix.toLowerCase() + "@demo.com");
          restaurant.setPhone("+1234567890");
          restaurant.setAddress("123 Main St");
          restaurant.setCurrency(Currency.EUR);
          restaurant.setAveragePrice(AveragePrice.PRICE_5_20);
          restaurant.setServiceFee(10);
          restaurant.setDefaultLanguage(Language.PT);
          restaurant.setCuisineType(CuisineType.BRAZILIAN);
          restaurant.setCustomerLanguages(List.of(Language.PT, Language.EN));
          restaurant.setTags(List.of(Tag.WIFI, Tag.PARKING, Tag.DELIVERY));
          restaurant.setDays(
              List.of(
                  new BusinessHours("monday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("tuesday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("wednesday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("thursday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("friday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("saturday", false, List.of(new Period("00:00", "23:59"))),
                  new BusinessHours("sunday", false, List.of(new Period("00:00", "23:59")))));
          restaurant.setThemeName(ThemeName.GOLDEN_HERITAGE);
          restaurant.setHashPrimaryColor("#000000");
          restaurant.setHashAccentColor("#FFFFFF");
          entityManager.persist(restaurant);

          RestaurantImageEntity image = new RestaurantImageEntity();
          image.setId(UUID.randomUUID());
          image.setRestaurantId(restaurant.getId());
          image.setName("gallery_image_1.jpg");
          image.setCover(false);
          entityManager.persist(image);
        });

    tenantProvisioningService.provisionTenant(restaurant.getId());
    String tenantId = TenantContext.generateTenantIdentifier(restaurant.getId());

    TenantContext.setCurrentTenant(tenantId);

    transactionTemplate.executeWithoutResult(
        status -> {
          entityManager.flush(); // Flush master data to public schema
          if (isH2) {
            entityManager.createNativeQuery("SET SCHEMA " + tenantId).executeUpdate();
          } else {
            entityManager.createNativeQuery("SET SCHEMA '" + tenantId + "'").executeUpdate();
          }

          CategoryEntity category = new CategoryEntity();
          category.setId(UUID.randomUUID());
          category.setRestaurantId(restaurant.getId());
          category.setActive(true);
          category.setNumOrder(1);
          category.setName(
              LocalizedTextEntity.fromMap(
                  Map.of(
                      Language.PT, "Entradas " + prefix,
                      Language.EN, "Starters " + prefix)));
          entityManager.persist(category);

          ItemEntity item = new ItemEntity();
          item.setId(UUID.randomUUID());
          item.setCategory(category);
          item.setActive(true);
          item.setPrice(new BigDecimal("10.00"));
          item.setName(
              LocalizedTextEntity.fromMap(
                  Map.of(
                      Language.PT, "Item " + prefix,
                      Language.EN, prefix + " Item")));
          item.setDescription(
              LocalizedTextEntity.fromMap(
                  Map.of(
                      Language.PT, "Descrição do item",
                      Language.EN, "Item description")));
          entityManager.persist(item);

          if (plan == Plan.PROFESSIONAL) {
            for (int i = 1; i <= 10; i++) {
              TableEntity table = new TableEntity();
              table.setId(UUID.randomUUID());
              table.setRestaurantId(restaurant.getId());
              table.setCod(String.format("%02d", i));
              table.setStatus(TableStatus.AVAILABLE);
              entityManager.persist(table);
            }
          }

          entityManager.flush(); // Flush tenant data to tenant schema
          if (isH2) {
            entityManager.createNativeQuery("SET SCHEMA public").executeUpdate();
          } else {
            entityManager.createNativeQuery("SET SCHEMA 'public'").executeUpdate();
          }
        });

    TenantContext.clear();
    return new AccountData(
        account.getId(), restaurant.getId(), restaurant.getSlug(), user.getId(), user.getEmail());
  }

  protected record AccountData(
      UUID accountId, UUID restaurantId, String slug, UUID userId, String email) {}

  protected void setTenant(UUID restaurantId) {
    String tenantId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.generateTenantIdentifier(
            restaurantId);
    dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.setCurrentTenant(tenantId);
  }

  protected void clearTenant() {
    TenantContext.clear();
  }
}
