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
  private final OrderEntityMapper mapper;

  public OrderRepositoryAdapter(
      OrderJpaRepository orderJpaRepository,
      ItemJpaRepository itemJpaRepository,
      RestaurantJpaRepository restaurantJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher,
      TicketJpaRepository ticketJpaRepository,
      OrderEntityMapper mapper) {
    this.orderJpaRepository = orderJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
    this.ticketJpaRepository = ticketJpaRepository;
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
}
