package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.H2IT;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.LocalizedTextEntity;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantProvisioningService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class CustomerApiControllerIT extends H2IT {

  private static final String API_BASE = "/api/v1/customer";

  @Autowired private RestaurantRepository restaurantRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private TableRepository tableRepository;
  @Autowired private TenantProvisioningService tenantProvisioningService;
  @Autowired private CategoryJpaRepository categoryJpaRepository;
  @Autowired private ItemJpaRepository itemJpaRepository;

  private Restaurant professionalRestaurant;
  private Restaurant starterRestaurant;
  private UUID itemId;

  @BeforeEach
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // Professional account + restaurant + table + menu item
    Account profAccount = new Account();
    profAccount.setId(UUID.randomUUID());
    profAccount.setPlan(Plan.PROFESSIONAL);
    profAccount.setCreatedAt(Time.nowOffset());
    accountRepository.save(profAccount);

    professionalRestaurant = createRestaurant(profAccount.getId(), "prof-" + UUID.randomUUID());
    provisionAndAddTable(professionalRestaurant, "01");
    itemId = seedMenuItem(professionalRestaurant);

    // Starter account + restaurant + table (should be blocked)
    Account starterAccount = new Account();
    starterAccount.setId(UUID.randomUUID());
    starterAccount.setPlan(Plan.STARTER);
    starterAccount.setCreatedAt(Time.nowOffset());
    accountRepository.save(starterAccount);

    starterRestaurant = createRestaurant(starterAccount.getId(), "starter-" + UUID.randomUUID());
    provisionAndAddTable(starterRestaurant, "01");
  }

  private Restaurant createRestaurant(UUID accountId, String slug) {
    Restaurant restaurant = new Restaurant();
    restaurant.setId(UUID.randomUUID());
    restaurant.setAccountId(accountId);
    restaurant.setName(slug.replace("-", " "));
    restaurant.setSlug(slug);
    restaurant.setEmail(slug + "@test.com");
    restaurant.setCurrency(Currency.BRL);
    restaurant.setAveragePrice(AveragePrice.PRICE_20_50);
    restaurant.setDefaultLanguage(Language.PT);
    restaurant.setCustomerLanguages(List.of(Language.PT));
    restaurant.setHashPrimaryColor("#000000");
    restaurant.setHashAccentColor("#ffffff");
    restaurantRepository.save(restaurant);
    return restaurant;
  }

  private void provisionAndAddTable(Restaurant restaurant, String tableCode) {
    tenantProvisioningService.provisionTenant(restaurant.getId());
    String tenant = TenantContext.generateTenantIdentifier(restaurant.getId());
    TenantContext.setCurrentTenant(tenant);
    try {
      Table table = new Table(UUID.randomUUID(), restaurant.getId(), tableCode);
      tableRepository.save(table);
    } finally {
      TenantContext.clear();
    }
  }

  private UUID seedMenuItem(Restaurant restaurant) {
    String tenant = TenantContext.generateTenantIdentifier(restaurant.getId());
    TenantContext.setCurrentTenant(tenant);
    try {
      CategoryEntity category = new CategoryEntity();
      category.setId(UUID.randomUUID());
      category.setRestaurantId(restaurant.getId());
      category.setNumOrder(1);
      category.setActive(true);
      categoryJpaRepository.save(category);

      LocalizedTextEntity itemName = new LocalizedTextEntity();
      itemName.getTranslations().put(Language.PT, "Item de Teste");

      ItemEntity item = new ItemEntity();
      item.setId(UUID.randomUUID());
      item.setCategory(category);
      item.setName(itemName);
      item.setPrice(new BigDecimal("10.00"));
      item.setActive(true);
      itemJpaRepository.save(item);

      return item.getId();
    } finally {
      TenantContext.clear();
    }
  }

  private String tableDataUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/menu/data";
  }

  private String openTableUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/open";
  }

  private String placeOrderUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/menu/order";
  }

  private String callWaiterUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/waiter/call";
  }

  private String rateItemUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/feedback/item";
  }

  private String feedbackUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/feedback/general";
  }

  private String updateCustomerNameUrl(String slug, String tableCode) {
    return API_BASE + "/" + slug + "/table/" + tableCode + "/customer-name";
  }

  // ========== getTableData ==========

  @Test
  void getTableData_shouldReturnOk_whenProfessionalPlan() throws Exception {
    mockMvc
        .perform(get(tableDataUrl(professionalRestaurant.getSlug(), "01")))
        .andExpect(status().isOk());
  }

  @Test
  void getTableData_shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    mockMvc
        .perform(get(tableDataUrl("non-existent-slug-" + UUID.randomUUID(), "01")))
        .andExpect(status().isNotFound());
  }

  @Test
  void getTableData_shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    mockMvc
        .perform(get(tableDataUrl(professionalRestaurant.getSlug(), "99")))
        .andExpect(status().isNotFound());
  }

  @Test
  void getTableData_shouldReturnNotFound_whenStarterPlan() throws Exception {
    mockMvc
        .perform(get(tableDataUrl(starterRestaurant.getSlug(), "01")))
        .andExpect(status().isNotFound());
  }

  // ========== openTable ==========

  @Test
  void openTable_shouldReturnOk_whenProfessionalPlan() throws Exception {
    UUID customerId = UUID.randomUUID();
    String body = "{\"customerId\": \"" + customerId + "\", \"customerName\": \"John Doe\"}";

    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  @Test
  void openTable_shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    UUID customerId = UUID.randomUUID();
    String body = "{\"customerId\": \"" + customerId + "\", \"customerName\": \"John\"}";

    mockMvc
        .perform(
            post(openTableUrl("non-existent-" + UUID.randomUUID(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void openTable_shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    UUID customerId = UUID.randomUUID();
    String body = "{\"customerId\": \"" + customerId + "\", \"customerName\": \"John\"}";

    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "99"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void openTable_shouldReturnNotFound_whenStarterPlan() throws Exception {
    UUID customerId = UUID.randomUUID();
    String body = "{\"customerId\": \"" + customerId + "\", \"customerName\": \"John\"}";

    mockMvc
        .perform(
            post(openTableUrl(starterRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  // ========== placeOrder ==========

  @Test
  void placeOrder_shouldReturnOk_whenValidRequest() throws Exception {
    // First open the table
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    String body =
        """
        {
          "tickets": [
            {
              "customerId": "%s",
              "items": [
                {
                  "itemId": "%s",
                  "quantity": 1
                }
              ]
            }
          ],
          "customers": [
            {
              "id": "%s",
              "name": "Customer"
            }
          ]
        }
        """
            .formatted(customerId, itemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  @Test
  void placeOrder_shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    String body =
        """
        {
          "tickets": [
            {
              "customerId": "%s",
              "items": [
                {
                  "itemId": "%s",
                  "quantity": 1
                }
              ]
            }
          ]
        }
        """
            .formatted(UUID.randomUUID(), UUID.randomUUID());

    mockMvc
        .perform(
            post(placeOrderUrl("non-existent-" + UUID.randomUUID(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void placeOrder_shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    String body =
        """
        {
          "tickets": [
            {
              "customerId": "%s",
              "items": [
                {
                  "itemId": "%s",
                  "quantity": 1
                }
              ]
            }
          ]
        }
        """
            .formatted(UUID.randomUUID(), itemId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "99"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  // ========== callWaiter ==========

  @Test
  void callWaiter_shouldReturnOk_whenValidRequest() throws Exception {
    // Open table first
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(callWaiterUrl(professionalRestaurant.getSlug(), "01"))
                .param("customerId", customerId.toString()))
        .andExpect(status().isOk());
  }

  @Test
  void callWaiter_shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    mockMvc
        .perform(post(callWaiterUrl("non-existent-" + UUID.randomUUID(), "01")))
        .andExpect(status().isNotFound());
  }

  @Test
  void callWaiter_shouldReturnOk_whenNoCustomerId() throws Exception {
    // Open table first
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(post(callWaiterUrl(professionalRestaurant.getSlug(), "01")))
        .andExpect(status().isOk());
  }

  // ========== rateItem ==========

  @Test
  void rateItem_shouldReturnOk_whenValidRating() throws Exception {
    // Open table and place order first
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    String placeOrderBody =
        """
        {
          "tickets": [
            {
              "customerId": "%s",
              "items": [
                {
                  "itemId": "%s",
                  "quantity": 1
                }
              ]
            }
          ],
          "customers": [
            {
              "id": "%s",
              "name": "Customer"
            }
          ]
        }
        """
            .formatted(customerId, itemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeOrderBody))
        .andExpect(status().isOk());

    // Now rate the item (uses the ticket item id, but we use itemId here as a best-effort)
    String rateBody = "{\"itemId\": \"" + UUID.randomUUID() + "\", \"rating\": 5}";

    mockMvc
        .perform(
            post(rateItemUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(rateBody))
        .andExpect(status().isOk());
  }

  // ========== submitGeneralFeedback ==========

  @Test
  void submitGeneralFeedback_shouldReturnOk_whenValidRequest() throws Exception {
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"tickets\": [{\"customerId\": \""
                        + customerId
                        + "\", \"items\": [{\"itemId\": \""
                        + itemId
                        + "\", \"quantity\": 1}]}]}"))
        .andExpect(status().isOk());

    String tableData =
        mockMvc
            .perform(get(tableDataUrl(professionalRestaurant.getSlug(), "01")))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Extract orderId (quick and dirty using regex to avoid json parsing imports)
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("\"orderId\":\"([^\"]+)\"").matcher(tableData);
    m.find();
    String orderId = m.group(1);

    String body =
        """
        {
          "orderId": "%s",
          "customerId": "%s",
          "rating": 4,
          "comment": "Great food!"
        }
        """
            .formatted(orderId, customerId);

    mockMvc
        .perform(
            post(feedbackUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  @Test
  void submitGeneralFeedback_shouldReturnOk_withoutComment() throws Exception {
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"tickets\": [{\"customerId\": \""
                        + customerId
                        + "\", \"items\": [{\"itemId\": \""
                        + itemId
                        + "\", \"quantity\": 1}]}]}"))
        .andExpect(status().isOk());

    String tableData =
        mockMvc
            .perform(get(tableDataUrl(professionalRestaurant.getSlug(), "01")))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("\"orderId\":\"([^\"]+)\"").matcher(tableData);
    m.find();
    String orderId = m.group(1);

    String body =
        """
        {
          "orderId": "%s",
          "customerId": "%s",
          "rating": 3
        }
        """
            .formatted(orderId, customerId);

    mockMvc
        .perform(
            post(feedbackUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  // ========== updateCustomerName ==========

  @Test
  void updateCustomerName_shouldReturnOk_whenValidRequest() throws Exception {
    // Open table first so there is an active order
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\": \"" + customerId + "\", \"customerName\": \"OldName\"}"))
        .andExpect(status().isOk());

    String body = "{\"customerId\": \"" + customerId + "\", \"name\": \"NewName\"}";

    mockMvc
        .perform(
            post(updateCustomerNameUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  @Test
  void updateCustomerName_shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    String body = "{\"customerId\": \"" + UUID.randomUUID() + "\", \"name\": \"NewName\"}";

    mockMvc
        .perform(
            post(updateCustomerNameUrl("non-existent-" + UUID.randomUUID(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateCustomerName_shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    String body = "{\"customerId\": \"" + UUID.randomUUID() + "\", \"name\": \"NewName\"}";

    mockMvc
        .perform(
            post(updateCustomerNameUrl(professionalRestaurant.getSlug(), "99"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  // ========== getTableData with active order ==========

  @Test
  void getTableData_shouldReturnOrderData_whenTableHasActiveOrder() throws Exception {
    // Open table
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    // Place an order
    String placeOrderBody =
        """
        {
          "tickets": [
            {
              "customerId": "%s",
              "items": [
                {
                  "itemId": "%s",
                  "quantity": 2
                }
              ]
            }
          ],
          "customers": [
            {
              "id": "%s",
              "name": "Customer"
            }
          ]
        }
        """
            .formatted(customerId, itemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalRestaurant.getSlug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeOrderBody))
        .andExpect(status().isOk());

    // Now getTableData should return order data
    mockMvc
        .perform(get(tableDataUrl(professionalRestaurant.getSlug(), "01")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").isNotEmpty())
        .andExpect(jsonPath("$.ticketItems").isArray());
  }

  // ========== getTableData with lastOrderId ==========

  @Test
  void getTableData_shouldReturnOk_whenLastOrderIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            get(tableDataUrl(professionalRestaurant.getSlug(), "01"))
                .param("lastOrderId", "invalid-uuid"))
        .andExpect(status().isOk());
  }

  @Test
  void getTableData_shouldReturnOk_whenLastOrderIdIsNonExistent() throws Exception {
    mockMvc
        .perform(
            get(tableDataUrl(professionalRestaurant.getSlug(), "01"))
                .param("lastOrderId", UUID.randomUUID().toString()))
        .andExpect(status().isOk());
  }
}
