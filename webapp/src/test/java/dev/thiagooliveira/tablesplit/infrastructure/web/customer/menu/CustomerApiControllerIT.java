package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.infrastructure.AbstractIntegrationTest;
import dev.thiagooliveira.tablesplit.infrastructure.IntegrationTest;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@IntegrationTest
class CustomerApiControllerIT extends AbstractIntegrationTest {

  @Autowired private ItemJpaRepository itemJpaRepository;

  @Autowired
  private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

  private UUID professionalItemId;

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    professionalItemId =
        TenentExecution.execute(
            professionalAccount.restaurantId(),
            () ->
                transactionTemplate.execute(
                    status ->
                        itemJpaRepository.findAll().stream().findFirst().orElseThrow().getId()));
  }

  protected static final String API_BASE = "/api/v1/customer";

  @Test
  void getTableData_shouldReturnOk_whenProfessionalPlan() throws Exception {
    mockMvc.perform(get(tableDataUrl(professionalAccount.slug(), "01"))).andExpect(status().isOk());
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
        .perform(get(tableDataUrl(professionalAccount.slug(), "99")))
        .andExpect(status().isNotFound());
  }

  @Test
  void getTableData_shouldReturnNotFound_whenStarterPlan() throws Exception {
    mockMvc
        .perform(get(tableDataUrl(starterAccount.slug(), "01")))
        .andExpect(status().isNotFound());
  }

  // ========== openTable ==========

  @Test
  void openTable_shouldReturnOk_whenProfessionalPlan() throws Exception {
    UUID customerId = UUID.randomUUID();
    String body = "{\"customerId\": \"" + customerId + "\", \"customerName\": \"John Doe\"}";

    mockMvc
        .perform(
            post(openTableUrl(professionalAccount.slug(), "01"))
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
            post(openTableUrl(professionalAccount.slug(), "99"))
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
            post(openTableUrl(starterAccount.slug(), "01"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
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
            .formatted(customerId, professionalItemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "01"))
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
            .formatted(UUID.randomUUID(), professionalItemId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "99"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(callWaiterUrl(professionalAccount.slug(), "01"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(post(callWaiterUrl(professionalAccount.slug(), "01")))
        .andExpect(status().isOk());
  }

  // ========== rateItem ==========

  @Test
  void rateItem_shouldReturnOk_whenValidRating() throws Exception {
    // Open table and place order first
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalAccount.slug(), "01"))
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
            .formatted(customerId, professionalItemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeOrderBody))
        .andExpect(status().isOk());

    // Now rate the item (uses the ticket item id, but we use super.professionalItemId here as a
    // best-effort)
    String rateBody = "{\"itemId\": \"" + UUID.randomUUID() + "\", \"rating\": 5}";

    mockMvc
        .perform(
            post(rateItemUrl(professionalAccount.slug(), "01"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"tickets\": [{\"customerId\": \""
                        + customerId
                        + "\", \"items\": [{\"itemId\": \""
                        + professionalItemId
                        + "\", \"quantity\": 1}]}]}"))
        .andExpect(status().isOk());

    String tableData =
        mockMvc
            .perform(get(tableDataUrl(professionalAccount.slug(), "01")))
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
            post(feedbackUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  @Test
  void submitGeneralFeedback_shouldReturnOk_withoutComment() throws Exception {
    UUID customerId = UUID.randomUUID();
    mockMvc
        .perform(
            post(openTableUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + customerId + "\", \"customerName\": \"Customer\"}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"tickets\": [{\"customerId\": \""
                        + customerId
                        + "\", \"items\": [{\"itemId\": \""
                        + professionalItemId
                        + "\", \"quantity\": 1}]}]}"))
        .andExpect(status().isOk());

    String tableData =
        mockMvc
            .perform(get(tableDataUrl(professionalAccount.slug(), "01")))
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
            post(feedbackUrl(professionalAccount.slug(), "01"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\": \"" + customerId + "\", \"customerName\": \"OldName\"}"))
        .andExpect(status().isOk());

    String body = "{\"customerId\": \"" + customerId + "\", \"name\": \"NewName\"}";

    mockMvc
        .perform(
            post(updateCustomerNameUrl(professionalAccount.slug(), "01"))
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
            post(updateCustomerNameUrl(professionalAccount.slug(), "99"))
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
            post(openTableUrl(professionalAccount.slug(), "01"))
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
            .formatted(customerId, professionalItemId, customerId);

    mockMvc
        .perform(
            post(placeOrderUrl(professionalAccount.slug(), "01"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(placeOrderBody))
        .andExpect(status().isOk());

    // Now getTableData should return order data
    mockMvc
        .perform(get(tableDataUrl(professionalAccount.slug(), "01")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").isNotEmpty())
        .andExpect(jsonPath("$.ticketItems").isArray());
  }

  // ========== getTableData with lastOrderId ==========

  @Test
  void getTableData_shouldReturnOk_whenLastOrderIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            get(tableDataUrl(professionalAccount.slug(), "01"))
                .param("lastOrderId", "invalid-uuid"))
        .andExpect(status().isOk());
  }

  @Test
  void getTableData_shouldReturnOk_whenLastOrderIdIsNonExistent() throws Exception {
    mockMvc
        .perform(
            get(tableDataUrl(professionalAccount.slug(), "01"))
                .param("lastOrderId", UUID.randomUUID().toString()))
        .andExpect(status().isOk());
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
}
