package dev.thiagooliveira.tablesplit.infrastructure.order.persistence;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence.RestaurantJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;
  private final ItemJpaRepository itemJpaRepository;
  private final RestaurantJpaRepository restaurantJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;
  private final TicketJpaRepository ticketJpaRepository;
  private final TableJpaRepository tableJpaRepository;
  private final OrderEntityMapper mapper;

  public OrderRepositoryAdapter(
      OrderJpaRepository orderJpaRepository,
      ItemJpaRepository itemJpaRepository,
      RestaurantJpaRepository restaurantJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher,
      TicketJpaRepository ticketJpaRepository,
      TableJpaRepository tableJpaRepository,
      OrderEntityMapper mapper) {
    this.orderJpaRepository = orderJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
    this.ticketJpaRepository = ticketJpaRepository;
    this.tableJpaRepository = tableJpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return orderJpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  private Order toDomainWithAccount(OrderEntity entity) {
    return toDomainList(List.of(entity)).get(0);
  }

  private List<Order> toDomainList(List<OrderEntity> entities) {
    if (entities == null || entities.isEmpty()) return java.util.Collections.emptyList();

    List<Order> domains = entities.stream().map(mapper::toDomain).toList();

    // 1. Batch fetch AccountIds
    java.util.Set<UUID> restaurantIds =
        domains.stream()
            .map(dev.thiagooliveira.tablesplit.domain.order.Order::getRestaurantId)
            .collect(java.util.stream.Collectors.toSet());
    java.util.Map<UUID, UUID> accountIdMap = new java.util.HashMap<>();
    java.util.Set<UUID> missingRids = new java.util.HashSet<>();

    for (UUID rid : restaurantIds) {
      UUID cached =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(rid);
      if (cached != null) {
        accountIdMap.put(rid, cached);
      } else {
        missingRids.add(rid);
      }
    }

    if (!missingRids.isEmpty()) {
      restaurantJpaRepository
          .findAllById(missingRids)
          .forEach(
              r -> {
                accountIdMap.put(r.getId(), r.getAccountId());
                dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.setAccountId(
                    r.getId(), r.getAccountId());
              });
    }

    // 2. Collect all item IDs for batch name lookup
    java.util.Set<UUID> itemIds = new java.util.HashSet<>();
    domains.forEach(
        order -> {
          order.setAccountId(accountIdMap.get(order.getRestaurantId()));
          if (order.getTickets() == null) return;
          order
              .getTickets()
              .forEach(
                  t -> {
                    if (t.getItems() == null) return;
                    t.getItems()
                        .forEach(
                            i -> {
                              if (i.getName() == null || i.getName().isEmpty()) {
                                itemIds.add(i.getItemId());
                              }
                            });
                  });
        });

    // 3. Batch fetch Item names
    if (!itemIds.isEmpty()) {
      java.util.Map<
              UUID, java.util.Map<dev.thiagooliveira.tablesplit.domain.common.Language, String>>
          nameMap =
              itemJpaRepository.findAllById(itemIds).stream()
                  .collect(
                      java.util.stream.Collectors.toMap(
                          ItemEntity::getId,
                          i ->
                              i.getName() != null
                                  ? i.getName().getTranslations()
                                  : new java.util.HashMap<>()));

      domains.forEach(
          order -> {
            if (order.getTickets() == null) return;
            order
                .getTickets()
                .forEach(
                    t -> {
                      if (t.getItems() == null) return;
                      t.getItems()
                          .forEach(
                              i -> {
                                if (i.getName() == null || i.getName().isEmpty()) {
                                  i.setName(
                                      nameMap.getOrDefault(
                                          i.getItemId(), new java.util.HashMap<>()));
                                }
                              });
                    });
          });
    }

    return domains;
  }

  @Override
  public Optional<Order> findActiveOrderByTableId(UUID tableId) {
    return orderJpaRepository
        .findByTableIdAndStatus(tableId, OrderStatus.OPEN)
        .map(this::toDomainWithAccount);
  }

  @Override
  public List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId) {
    return toDomainList(orderJpaRepository.findAllByTableIdOrderByOpenedAtDesc(tableId));
  }

  @Override
  public List<Order> findAllByTableIdAndStatusAndOpenedAtBetween(
      UUID tableId,
      OrderStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return toDomainList(orderJpaRepository.findAllFiltered(tableId, status, start, end));
  }

  @Override
  public void save(Order order) {
    orderJpaRepository.save(mapper.toEntity(order));

    // Ensure accountId is populated for events
    if (order.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              order.getRestaurantId());
      order.setAccountId(cachedAccountId);
    }

    // Publish Domain Events
    order.getDomainEvents().forEach(eventPublisher::publishEvent);
    order.clearEvents();
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status) {
    return toDomainList(orderJpaRepository.findAllByRestaurantIdAndStatus(restaurantId, status));
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatusAndClosedAtAfter(
      UUID restaurantId, OrderStatus status, java.time.ZonedDateTime threshold) {
    return toDomainList(
        orderJpaRepository.findAllByRestaurantIdAndStatusAndClosedAtAfter(
            restaurantId, status, threshold));
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatusAndClosedAtBetween(
      UUID restaurantId,
      OrderStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return toDomainList(
        orderJpaRepository.findAllByRestaurantIdAndStatusAndClosedAtBetween(
            restaurantId, status, start, end));
  }

  @Override
  public Optional<Order> findByTicketId(UUID ticketId) {
    return orderJpaRepository.findByTicketId(ticketId).map(this::toDomainWithAccount);
  }

  @Override
  public Optional<Order> findByTicketItemId(UUID itemId) {
    return orderJpaRepository.findByTicketItemId(itemId).map(this::toDomainWithAccount);
  }

  @Override
  public void delete(UUID id) {
    orderJpaRepository.deleteById(id);
  }

  @Override
  public long countTicketsByStatus(
      UUID restaurantId,
      dev.thiagooliveira.tablesplit.domain.order.TicketStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return ticketJpaRepository.countByOrderRestaurantIdAndStatusAndCreatedAtBetween(
        restaurantId, status, start, end);
  }

  @Override
  public dev.thiagooliveira.tablesplit.domain.common.Pagination<
          dev.thiagooliveira.tablesplit.domain.order.TicketWithTable>
      findHistoryTickets(
          UUID restaurantId,
          java.time.ZonedDateTime start,
          java.time.ZonedDateTime end,
          int page,
          int size) {

    org.springframework.data.domain.Pageable pageable =
        org.springframework.data.domain.PageRequest.of(page, size);

    List<dev.thiagooliveira.tablesplit.domain.order.TicketStatus> statuses =
        List.of(
            dev.thiagooliveira.tablesplit.domain.order.TicketStatus.DELIVERED,
            dev.thiagooliveira.tablesplit.domain.order.TicketStatus.CANCELLED);

    org.springframework.data.domain.Page<TicketEntity> ticketPage =
        ticketJpaRepository.findHistory(restaurantId, statuses, start, end, pageable);

    // 1. Get unique orders to batch fetch them (with all items, customers, etc)
    List<OrderEntity> orderEntities =
        ticketPage.getContent().stream().map(TicketEntity::getOrder).distinct().toList();

    List<Order> orders = toDomainList(orderEntities);
    java.util.Map<UUID, Order> orderMap =
        orders.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    dev.thiagooliveira.tablesplit.domain.order.Order::getId, o -> o));

    // 2. Get table codes
    java.util.Set<UUID> tableIds =
        orders.stream()
            .map(dev.thiagooliveira.tablesplit.domain.order.Order::getTableId)
            .collect(java.util.stream.Collectors.toSet());
    java.util.Map<UUID, String> tableCodMap =
        tableJpaRepository.findAllById(tableIds).stream()
            .collect(java.util.stream.Collectors.toMap(TableEntity::getId, TableEntity::getCod));

    // 3. Map back to TicketWithTable
    List<dev.thiagooliveira.tablesplit.domain.order.TicketWithTable> items =
        ticketPage.getContent().stream()
            .map(
                te -> {
                  Order order = orderMap.get(te.getOrder().getId());
                  dev.thiagooliveira.tablesplit.domain.order.Ticket ticket =
                      order.getTickets().stream()
                          .filter(t -> t.getId().equals(te.getId()))
                          .findFirst()
                          .orElseThrow();
                  String tableCod = tableCodMap.getOrDefault(order.getTableId(), "??");
                  return new dev.thiagooliveira.tablesplit.domain.order.TicketWithTable(
                      ticket, order, tableCod);
                })
            .toList();

    return new dev.thiagooliveira.tablesplit.domain.common.Pagination<>(
        items,
        ticketPage.getNumber(),
        ticketPage.getTotalPages(),
        ticketPage.getTotalElements(),
        ticketPage.getSize(),
        ticketPage.hasNext());
  }

  @Override
  public HistorySummary getHistorySummary(
      UUID restaurantId, java.time.ZonedDateTime start, java.time.ZonedDateTime end) {
    List<dev.thiagooliveira.tablesplit.domain.order.TicketStatus> statuses =
        List.of(
            dev.thiagooliveira.tablesplit.domain.order.TicketStatus.DELIVERED,
            dev.thiagooliveira.tablesplit.domain.order.TicketStatus.CANCELLED);

    long totalOrders = ticketJpaRepository.countHistory(restaurantId, statuses, start, end);
    Double revenue = ticketJpaRepository.sumRevenue(restaurantId, statuses, start, end);
    java.math.BigDecimal totalRevenue =
        revenue != null ? java.math.BigDecimal.valueOf(revenue) : java.math.BigDecimal.ZERO;

    java.math.BigDecimal avgTicket =
        totalOrders == 0
            ? java.math.BigDecimal.ZERO
            : totalRevenue.divide(
                java.math.BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP);

    return new HistorySummary(totalOrders, totalRevenue, avgTicket);
  }
}
