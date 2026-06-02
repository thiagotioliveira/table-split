package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.PaymentMethod;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.AbstractInitDatabaseStringTest;
import dev.thiagooliveira.tablesplit.infrastructure.IntegrationTest;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.order.persistence.*;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.CustomUserDetailsService;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class OrderCleanerApiControllerIT extends AbstractInitDatabaseStringTest {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private OrderJpaRepository orderJpaRepository;
  @Autowired private TableJpaRepository tableJpaRepository;
  @Autowired private RestaurantJpaRepository restaurantJpaRepository;

  @Autowired private OrderFeedbackJpaRepository feedbackJpaRepository;

  @Autowired private CategoryJpaRepository categoryJpaRepository;

  @Autowired private ItemJpaRepository itemJpaRepository;

  @Autowired private Environment environment;

  @Autowired private CustomUserDetailsService userDetailsService;

  private UUID restaurantId;
  private String tenantSchema;

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    authenticatedWith(professionalAccount.email());
    restaurantId = accountContext.getRestaurant().getId();
    tenantSchema = TenantContext.generateTenantIdentifier(restaurantId);

    seedTestData();
  }

  private void seedTestData() {
    String zoneId = environment.getProperty("app.time.zone-id", "UTC");
    ZoneId zone = ZoneId.of(zoneId);

    TenantContext.setCurrentTenant(tenantSchema);
    try {
      UUID tableId =
          tableJpaRepository.findAll().stream()
              .filter(t -> "01".equals(t.getCod()))
              .findFirst()
              .map(TableEntity::getId)
              .orElseThrow(() -> new RuntimeException("Table 01 not found"));

      var itemEntity = itemJpaRepository.findAll().stream().findFirst().orElseThrow();

      // OLD ORDER (to be cleaned)
      OrderEntity oldOrder = new OrderEntity();
      UUID oldOrderId = UUID.randomUUID();
      oldOrder.setId(oldOrderId);
      oldOrder.setRestaurantId(restaurantId);
      oldOrder.setTableId(tableId);
      oldOrder.setStatus(OrderStatus.CLOSED);
      oldOrder.setOpenedAt(ZonedDateTime.now(zone).minusDays(100).minusHours(1));
      oldOrder.setClosedAt(ZonedDateTime.now(zone).minusDays(100));
      oldOrder.setServiceFee(10);

      // Add Ticket
      TicketEntity ticket = new TicketEntity();
      ticket.setId(UUID.randomUUID());
      ticket.setOrder(oldOrder);
      ticket.setStatus(TicketStatus.READY);
      ticket.setCreatedAt(oldOrder.getOpenedAt());

      TicketItemEntity ticketItem = new TicketItemEntity();
      ticketItem.setId(UUID.randomUUID());
      ticketItem.setTicket(ticket);
      ticketItem.setItemId(itemEntity.getId());
      ticketItem.setCustomerId(UUID.randomUUID());
      ticketItem.setQuantity(2);
      ticketItem.setUnitPrice(itemEntity.getPrice());
      ticketItem.setStatus(TicketStatus.READY);
      ticket.setItems(java.util.Set.of(ticketItem));
      oldOrder.setTickets(java.util.Set.of(ticket));

      // Add Payment
      PaymentEntity payment = new PaymentEntity();
      payment.setId(UUID.randomUUID());
      payment.setOrder(oldOrder);
      payment.setAmount(new BigDecimal("31.00"));
      payment.setPaidAt(oldOrder.getClosedAt());
      payment.setMethod(PaymentMethod.CASH);
      oldOrder.setPayments(Collections.singleton(payment));

      orderJpaRepository.save(oldOrder);

      // Add Feedback
      OrderFeedbackEntity feedback = new OrderFeedbackEntity();
      feedback.setId(UUID.randomUUID());
      feedback.setOrder(oldOrder);
      feedback.setCustomerId(ticketItem.getCustomerId());
      feedback.setRating(5);
      feedback.setComment("Great job!");
      feedback.setCreatedAt(oldOrder.getClosedAt());
      feedbackJpaRepository.save(feedback);

      // NEW ORDER (to stay)
      OrderEntity newOrder = new OrderEntity();
      newOrder.setId(UUID.randomUUID());
      newOrder.setRestaurantId(restaurantId);
      newOrder.setTableId(tableId);
      newOrder.setStatus(OrderStatus.CLOSED);
      newOrder.setOpenedAt(ZonedDateTime.now(zone).minusDays(5).minusHours(1));
      newOrder.setClosedAt(ZonedDateTime.now(zone).minusDays(5));
      newOrder.setServiceFee(10);
      orderJpaRepository.save(newOrder);

    } finally {
      TenantContext.clear();
    }
  }

  @Test
  void runCleaner_ShouldDeleteOldOrdersAndRelatedData_AndReturn200() throws Exception {
    assertThat(restaurantJpaRepository.findAll()).isNotEmpty();

    TenantContext.setCurrentTenant(tenantSchema);
    assertThat(orderJpaRepository.findAll()).hasSize(2);
    assertThat(feedbackJpaRepository.findAll()).hasSize(1);
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.tickets", tenantSchema), Integer.class))
        .isEqualTo(1);
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.ticket_items", tenantSchema), Integer.class))
        .isEqualTo(1);
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.payments", tenantSchema), Integer.class))
        .isEqualTo(1);
    TenantContext.clear();

    mockMvc
        .perform(post("/api/system/orders/cleaner/run").with(httpBasic("system", "supersecret")))
        .andExpect(status().isOk());

    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              TenantContext.setCurrentTenant(tenantSchema);
              try {
                List<OrderEntity> allAfter = orderJpaRepository.findAll();
                assertThat(allAfter).hasSize(1);

                // Verify cascade deletion and manual feedback deletion
                assertThat(feedbackJpaRepository.findAll()).isEmpty();
                assertThat(
                        jdbcTemplate.queryForObject(
                            String.format("SELECT count(*) FROM %s.tickets", tenantSchema),
                            Integer.class))
                    .isZero();
                assertThat(
                        jdbcTemplate.queryForObject(
                            String.format("SELECT count(*) FROM %s.ticket_items", tenantSchema),
                            Integer.class))
                    .isZero();
                assertThat(
                        jdbcTemplate.queryForObject(
                            String.format("SELECT count(*) FROM %s.payments", tenantSchema),
                            Integer.class))
                    .isZero();
              } finally {
                TenantContext.clear();
              }
            });
  }
}
