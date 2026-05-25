package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Period;
import dev.thiagooliveira.tablesplit.infrastructure.PostgresIT;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.LocalizedTextEntity;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.OrderJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.TableEntity;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.TableJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class FakeOrderApiControllerIT extends PostgresIT {

  @Autowired private RestaurantJpaRepository restaurantJpaRepository;
  @Autowired private TableJpaRepository tableJpaRepository;
  @Autowired private ItemJpaRepository itemJpaRepository;
  @Autowired private CategoryJpaRepository categoryJpaRepository;
  @Autowired private OrderJpaRepository orderJpaRepository;
  @Autowired private FakeOrderService fakeOrderService;

  private UUID restaurantId;
  private String tenantSchema;

  @BeforeEach
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    restaurantId = accountContext.getRestaurant().getId();
    tenantSchema = TenantContext.generateTenantIdentifier(restaurantId);

    // Override the demo restaurant ID in the service to match our test restaurant
    ReflectionTestUtils.setField(fakeOrderService, "demoRestaurantId", restaurantId.toString());

    seedTestData();
  }

  private void seedTestData() {
    TenantContext.setCurrentTenant(tenantSchema);
    try {
      // Ensure restaurant is open and has a default language
      RestaurantEntity restaurant = restaurantJpaRepository.findById(restaurantId).orElseThrow();
      restaurant.setDefaultLanguage(Language.PT);

      // Seed 24/7 business hours to make the test time-independent
      List<BusinessHours> businessHoursList = new ArrayList<>();
      for (DayOfWeek day : DayOfWeek.values()) {
        businessHoursList.add(
            new BusinessHours(
                day.name().toLowerCase(), false, List.of(new Period("00:00", "23:59"))));
      }
      restaurant.setDays(businessHoursList);

      restaurantJpaRepository.save(restaurant);

      // Create a table
      TableEntity table = new TableEntity();
      table.setId(UUID.randomUUID());
      table.setRestaurantId(restaurantId);
      table.setCod("F1");
      table.setStatus(dev.thiagooliveira.tablesplit.domain.order.TableStatus.AVAILABLE);
      tableJpaRepository.save(table);

      // Create a category and item
      CategoryEntity category = new CategoryEntity();
      category.setId(UUID.randomUUID());
      category.setRestaurantId(restaurantId);
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

    } finally {
      TenantContext.clear();
    }
  }

  @Test
  void runFakeOrderGeneration_ShouldCreateOrderAndRelatedData() throws Exception {
    // 1. Call the endpoint
    mockMvc
        .perform(post("/api/system/orders/fake/run").with(httpBasic("system", "supersecret")))
        .andExpect(status().isOk());

    // 2. Wait for the async process to complete
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(1, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              TenantContext.setCurrentTenant(tenantSchema);
              try {
                long orderCount = orderJpaRepository.count();
                assertThat(orderCount).isGreaterThan(0);
              } finally {
                TenantContext.clear();
              }
            });
  }
}
