package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository
      itemJpaRepository;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant
          .RestaurantJpaRepository
      restaurantJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;

  public OrderRepositoryAdapter(
      OrderJpaRepository orderJpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository
          itemJpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository
          restaurantJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    this.orderJpaRepository = orderJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return orderJpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  private Order toDomainWithAccount(OrderEntity entity) {
    Order domain = entity.toDomain();
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            domain.getRestaurantId());
    if (cachedAccountId != null) {
      domain.setAccountId(cachedAccountId);
    } else {
      this.restaurantJpaRepository
          .findById(domain.getRestaurantId())
          .ifPresent(r -> domain.setAccountId(r.getAccountId()));
    }
    return fillItemNames(domain);
  }

  @Override
  public Optional<Order> findActiveOrderByTableId(UUID tableId) {
    return orderJpaRepository
        .findByTableIdAndStatus(tableId, OrderStatus.OPEN)
        .map(this::toDomainWithAccount);
  }

  @Override
  public List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId) {
    return orderJpaRepository.findAllByTableIdOrderByOpenedAtDesc(tableId).stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  @Override
  public List<Order> findAllByTableIdAndStatusAndOpenedAtBetween(
      UUID tableId,
      OrderStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return orderJpaRepository.findAllFiltered(tableId, status, start, end).stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  @Override
  public void save(Order order) {
    orderJpaRepository.save(OrderEntity.fromDomain(order));

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
    return orderJpaRepository.findAllByRestaurantIdAndStatus(restaurantId, status).stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatusAndClosedAtAfter(
      UUID restaurantId, OrderStatus status, java.time.ZonedDateTime threshold) {
    return orderJpaRepository
        .findAllByRestaurantIdAndStatusAndClosedAtAfter(restaurantId, status, threshold)
        .stream()
        .map(this::toDomainWithAccount)
        .toList();
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

  private Order fillItemNames(Order order) {
    if (order == null || order.getTickets() == null) return order;

    java.util.Map<UUID, java.util.Map<dev.thiagooliveira.tablesplit.domain.common.Language, String>>
        nameCache = new java.util.HashMap<>();

    order
        .getTickets()
        .forEach(
            ticket -> {
              if (ticket.getItems() == null) return;
              ticket
                  .getItems()
                  .forEach(
                      item -> {
                        if (item.getName() == null || item.getName().isEmpty()) {
                          var name =
                              nameCache.computeIfAbsent(
                                  item.getItemId(),
                                  itemId ->
                                      itemJpaRepository
                                          .findById(itemId)
                                          .map(
                                              i ->
                                                  i.getName() != null
                                                      ? i.getName().getTranslations()
                                                      : new java.util.HashMap<
                                                          dev.thiagooliveira.tablesplit.domain
                                                              .common.Language,
                                                          String>())
                                          .orElse(new java.util.HashMap<>()));
                          item.setName(name);
                        }
                      });
            });

    return order;
  }
}
