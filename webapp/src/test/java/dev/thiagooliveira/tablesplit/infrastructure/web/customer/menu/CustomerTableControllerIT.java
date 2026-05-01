package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantProvisioningService;
import dev.thiagooliveira.tablesplit.infrastructure.web.H2IT;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class CustomerTableControllerIT extends H2IT {

  @Autowired private RestaurantRepository restaurantRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private TableRepository tableRepository;
  @Autowired private TenantProvisioningService tenantProvisioningService;

  private Restaurant professionalRestaurant;
  private Restaurant starterRestaurant;
  private Restaurant trialRestaurant;

  @BeforeEach
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // Setup Professional Account & Restaurant
    Account profAccount = new Account();
    profAccount.setId(UUID.randomUUID());
    profAccount.setPlan(Plan.PROFESSIONAL);
    profAccount.setCreatedAt(Time.nowOffset());
    accountRepository.save(profAccount);

    professionalRestaurant = createRestaurant(profAccount.getId(), "prof-" + UUID.randomUUID());
    provisionAndAddTable(professionalRestaurant, "01");

    // Setup Starter Account & Restaurant
    Account starterAccount = new Account();
    starterAccount.setId(UUID.randomUUID());
    starterAccount.setPlan(Plan.STARTER);
    starterAccount.setCreatedAt(Time.nowOffset());
    accountRepository.save(starterAccount);

    starterRestaurant = createRestaurant(starterAccount.getId(), "starter-" + UUID.randomUUID());
    provisionAndAddTable(starterRestaurant, "01");

    // Setup Trial Account & Restaurant
    Account trialAccount = new Account();
    trialAccount.setId(UUID.randomUUID());
    trialAccount.setPlan(Plan.STARTER);
    trialAccount.setCreatedAt(Time.nowOffset());
    trialAccount.startTrial();
    accountRepository.save(trialAccount);

    trialRestaurant = createRestaurant(trialAccount.getId(), "trial-" + UUID.randomUUID());
    provisionAndAddTable(trialRestaurant, "01");
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

  @Test
  void shouldAllowAccess_whenPlanIsProfessional() throws Exception {
    mockMvc
        .perform(get("/@" + professionalRestaurant.getSlug() + "/table/01"))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/@" + professionalRestaurant.getSlug() + "/table/01/menu"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/customer/" + professionalRestaurant.getSlug() + "/table/01/menu/data"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldAllowAccess_whenPlanIsTrial() throws Exception {
    mockMvc.perform(get("/@" + trialRestaurant.getSlug() + "/table/01")).andExpect(status().isOk());
  }

  @Test
  void shouldReturnNotFound_whenPlanIsStarter() throws Exception {
    mockMvc
        .perform(get("/@" + starterRestaurant.getSlug() + "/table/01"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/@" + starterRestaurant.getSlug() + "/menu"));

    mockMvc
        .perform(
            post("/api/v1/customer/" + starterRestaurant.getSlug() + "/table/01/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + UUID.randomUUID() + "\", \"customerName\": \"John\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/@non-existent-" + UUID.randomUUID() + "/table/01"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/@" + professionalRestaurant.getSlug() + "/table/99"))
        .andExpect(status().isNotFound());
  }
}
