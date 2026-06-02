package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.infrastructure.AbstractIntegrationTest;
import dev.thiagooliveira.tablesplit.infrastructure.IntegrationTest;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.OrderJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@IntegrationTest
class FakeOrderApiControllerIT extends AbstractIntegrationTest {

  @Autowired private OrderJpaRepository orderJpaRepository;
  @Autowired private FakeOrderService fakeOrderService;

  private String tenantSchema;

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();

    authenticatedWith(professionalAccount.email());
    var restaurantId = accountContext.getRestaurant().getId();
    tenantSchema = TenantContext.generateTenantIdentifier(restaurantId);

    // Override the demo restaurant ID in the service to match our test restaurant
    ReflectionTestUtils.setField(fakeOrderService, "demoRestaurantId", restaurantId.toString());
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
