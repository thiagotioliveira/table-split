package dev.thiagooliveira.tablesplit.infrastructure.web.cleaner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.domain.order.PaymentMethod;
import dev.thiagooliveira.tablesplit.domain.order.TicketStatus;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.order.*;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.PostgresIT;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

class OrderCleanerApiIT extends PostgresIT {

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
  protected void setUp() throws Exception {
    super.setUp();

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

      // Seed Menu Data
      CategoryEntity category = new CategoryEntity();
      category.setId(UUID.randomUUID());
      category.setRestaurantId(restaurantId);
      category.setNumOrder(1);
      category.setActive(true);
      categoryJpaRepository.save(category);

      ItemEntity itemEntity = new ItemEntity();
      itemEntity.setId(UUID.randomUUID());
      itemEntity.setCategory(category);
      itemEntity.setPrice(new BigDecimal("15.50"));
      itemEntity.setActive(true);
      itemJpaRepository.save(itemEntity);

      // OLD ORDER (to be cleaned)
      OrderEntity oldOrder = new OrderEntity();
      UUID oldOrderId = UUID.randomUUID();
      oldOrder.setId(oldOrderId);
      oldOrder.setRestaurantId(restaurantId);
      oldOrder.setTableId(tableId);
      oldOrder.setStatus(OrderStatus.CLOSED);
      oldOrder.setOpenedAt(ZonedDateTime.now(zone).minusDays(100).minusHours(1));
      oldOrder.setClosedAt(ZonedDateTime.now(zone).minusDays(100));
      oldOrder.setServiceFee(0);

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
      ticket.setItems(Collections.singletonList(ticketItem));
      oldOrder.setTickets(Collections.singletonList(ticket));

      // Add Payment
      PaymentEntity payment = new PaymentEntity();
      payment.setId(UUID.randomUUID());
      payment.setOrder(oldOrder);
      payment.setAmount(new BigDecimal("31.00"));
      payment.setPaidAt(oldOrder.getClosedAt());
      payment.setMethod(PaymentMethod.CASH);
      oldOrder.setPayments(Collections.singletonList(payment));

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
      newOrder.setServiceFee(0);
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
        .perform(post("/api/system/cleaner/run").with(httpBasic("system", "supersecret")))
        .andExpect(status().isOk());

    TenantContext.setCurrentTenant(tenantSchema);
    List<OrderEntity> allAfter = orderJpaRepository.findAll();
    assertThat(allAfter).hasSize(1);

    // Verify cascade deletion and manual feedback deletion
    assertThat(feedbackJpaRepository.findAll()).isEmpty();
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.tickets", tenantSchema), Integer.class))
        .isZero();
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.ticket_items", tenantSchema), Integer.class))
        .isZero();
    assertThat(
            jdbcTemplate.queryForObject(
                String.format("SELECT count(*) FROM %s.payments", tenantSchema), Integer.class))
        .isZero();

    TenantContext.clear();
  }
}
