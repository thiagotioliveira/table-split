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
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RegisterModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RestaurantModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.UserModel;
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

@Deprecated
public abstract class BaseRegisteredSpringTest extends BaseSpringTest {

  protected static final String PROFESSIONAL_REGISTERED_EMAIL = "prof.authenticated.it@example.com";
  protected static final String STARTER_REGISTERED_EMAIL = "starter.authenticated.it@example.com";
  protected String professionalSlug;
  protected String starterSlug;

  @Autowired private PendingRegistrationRepository pendingRegistrationRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired
  protected dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository
      restaurantRepository;

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
    try {
      var professionalResult =
          registerRestaurant(
              Plan.PROFESSIONAL, "Cantina Professional", PROFESSIONAL_REGISTERED_EMAIL);
      professionalSlug = professionalResult.model().getRestaurant().getSlug();

      var starterResult =
          registerRestaurant(Plan.STARTER, "Cantina Starter", STARTER_REGISTERED_EMAIL);
      starterSlug = starterResult.model().getRestaurant().getSlug();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected record RegistrationResult(
      RegisterModel model, org.springframework.mock.web.MockHttpSession session) {}

  private RegistrationResult registerRestaurant(Plan plan, String name, String email)
      throws Exception {
    RegisterModel registerModel = new RegisterModel();
    registerModel.setPlan(plan);

    UserModel userModel = new UserModel();
    userModel.setFirstName("Thiago");
    userModel.setLastName("Oliveira");
    String uniqueEmail = email;
    userModel.setEmail(uniqueEmail);
    userModel.setPhone("123456789");
    userModel.setPassword("password123");
    userModel.setLanguage(Language.PT.name());
    registerModel.setUser(userModel);

    RestaurantModel restaurantModel = new RestaurantModel();
    restaurantModel.setName(name);
    restaurantModel.setSlug(
        name.toLowerCase().replace(" ", "-")
            + "-"
            + java.util.UUID.randomUUID().toString().substring(0, 8));
    restaurantModel.setDescription("Descricao " + name);
    restaurantModel.setPhone("987654321");
    restaurantModel.setEmail("contato@" + email + ".com");
    restaurantModel.setWebsite("www." + email + ".com");
    restaurantModel.setAddress("Rua " + name + ", 123");
    restaurantModel.setCurrency(Currency.BRL);
    restaurantModel.setServiceFee(10);
    restaurantModel.setCuisineType(CuisineType.BRAZILIAN);
    restaurantModel.setAveragePrice(AveragePrice.PRICE_20_50);
    restaurantModel.setTags(List.of(RestaurantTag.WIFI, RestaurantTag.PARKING));
    restaurantModel.setNumberOfTables(10);
    registerModel.setRestaurant(restaurantModel);

    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("plan", registerModel.getPlan().name())
                .param("user.firstName", registerModel.getUser().getFirstName())
                .param("user.lastName", registerModel.getUser().getLastName())
                .param("user.email", registerModel.getUser().getEmail())
                .param("user.phone", registerModel.getUser().getPhone())
                .param("user.password", registerModel.getUser().getPassword())
                .param("user.language", registerModel.getUser().getLanguage())
                .param("restaurant.name", registerModel.getRestaurant().getName())
                .param("restaurant.slug", registerModel.getRestaurant().getSlug())
                .param("restaurant.description", registerModel.getRestaurant().getDescription())
                .param("restaurant.phone", registerModel.getRestaurant().getPhone())
                .param("restaurant.email", registerModel.getRestaurant().getEmail())
                .param("restaurant.website", registerModel.getRestaurant().getWebsite())
                .param("restaurant.address", registerModel.getRestaurant().getAddress())
                .param("restaurant.currency", registerModel.getRestaurant().getCurrency().name())
                .param(
                    "restaurant.serviceFee",
                    String.valueOf(registerModel.getRestaurant().getServiceFee()))
                .param(
                    "restaurant.cuisineType", registerModel.getRestaurant().getCuisineType().name())
                .param(
                    "restaurant.averagePrice",
                    registerModel.getRestaurant().getAveragePrice().name())
                .param(
                    "restaurant.tags",
                    registerModel.getRestaurant().getTags().get(0).name(),
                    registerModel.getRestaurant().getTags().get(1).name())
                .param(
                    "restaurant.numberOfTables",
                    String.valueOf(registerModel.getRestaurant().getNumberOfTables())))
        .andExpect(status().is3xxRedirection());

    var pending =
        pendingRegistrationRepository
            .findByEmail(uniqueEmail)
            .orElseThrow(
                () -> new IllegalStateException("Pending registration not found for test email"));

    mockMvc
        .perform(
            post("/register/verify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", uniqueEmail)
                .param("code", pending.getCode()))
        .andExpect(status().is3xxRedirection());

    org.springframework.test.web.servlet.MvcResult loginResult =
        mockMvc
            .perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", uniqueEmail)
                    .param("password", "password123"))
            .andExpect(status().is3xxRedirection())
            .andReturn();

    org.springframework.mock.web.MockHttpSession session =
        (org.springframework.mock.web.MockHttpSession) loginResult.getRequest().getSession();

    var restaurant =
        restaurantRepository.findBySlug(registerModel.getRestaurant().getSlug()).orElseThrow();

    org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder settingsRequest =
        post("/settings")
            .session(session)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", restaurant.getName())
            .param("slug", restaurant.getSlug())
            .param("description", restaurant.getDescription())
            .param("phone", restaurant.getPhone())
            .param("website", restaurant.getWebsite())
            .param("email", "valid-contact@tablesplit.app")
            .param(
                "address",
                restaurant.getAddress() != null && !restaurant.getAddress().isEmpty()
                    ? restaurant.getAddress()
                    : "123 Test St")
            .param(
                "cuisineType",
                restaurant.getCuisineType() != null ? restaurant.getCuisineType().name() : "")
            .param(
                "currency", restaurant.getCurrency() != null ? restaurant.getCurrency().name() : "")
            .param("serviceFee", String.valueOf(restaurant.getServiceFee()))
            .param(
                "averagePrice",
                restaurant.getAveragePrice() != null
                    ? restaurant.getAveragePrice().name()
                    : "CHEAP");

    if (restaurant.getTags() != null) {
      for (dev.thiagooliveira.tablesplit.domain.restaurant.Tag tag : restaurant.getTags()) {
        settingsRequest.param("tags", tag.name());
      }
    }

    settingsRequest
        .param("hashPrimaryColor", "#c9a050")
        .param("hashAccentColor", "#a88535")
        .param("customerLanguages", Language.PT.name(), Language.EN.name());

    if (restaurant.getDays() != null) {
      for (int i = 0; i < restaurant.getDays().size(); i++) {
        var day = restaurant.getDays().get(i);
        settingsRequest.param("days[" + i + "].day", day.getDay());
        settingsRequest.param("days[" + i + "].closed", String.valueOf(day.isClosed()));
        if (day.getPeriods() != null) {
          for (int j = 0; j < day.getPeriods().size(); j++) {
            var period = day.getPeriods().get(j);
            settingsRequest.param("days[" + i + "].periods[" + j + "].start", period.getStart());
            settingsRequest.param("days[" + i + "].periods[" + j + "].end", period.getEnd());
          }
        }
      }
    }

    mockMvc.perform(settingsRequest).andExpect(status().is3xxRedirection());

    org.springframework.mock.web.MockMultipartFile file =
        new org.springframework.mock.web.MockMultipartFile(
            "file", "gallery-image-1.jpg", "image/jpeg", "dummy content".getBytes());

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart(
                    "/gallery")
                .file(file)
                .session(session))
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(
                    "/menu/categories")
                .param("order", "1")
                .param("name[PT]", "Categoria de Teste")
                .session(session))
        .andExpect(status().is3xxRedirection());

    org.springframework.test.web.servlet.MvcResult menuResult =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/menu")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn();
    dev.thiagooliveira.tablesplit.infrastructure.menu.web.model.MenuModel menuModel =
        (dev.thiagooliveira.tablesplit.infrastructure.menu.web.model.MenuModel)
            menuResult.getModelAndView().getModel().get("menu");
    java.util.UUID categoryId = java.util.UUID.fromString(menuModel.getCategories().get(0).getId());

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart(
                    "/menu/items")
                .param("categoryId", categoryId.toString())
                .param("name[PT]", "Item de Teste")
                .param("price", "10.00")
                .param("available", "true")
                .session(session))
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/menu")
                .session(session))
        .andExpect(status().isOk());

    return new RegistrationResult(registerModel, session);
  }
}
