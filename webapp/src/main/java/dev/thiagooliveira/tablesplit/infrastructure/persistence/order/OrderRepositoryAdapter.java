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
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;

  public OrderRepositoryAdapter(
      OrderJpaRepository orderJpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository
          itemJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    this.orderJpaRepository = orderJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return orderJpaRepository.findById(id).map(OrderEntity::toDomain).map(this::fillItemNames);
  }

  @Override
  public Optional<Order> findActiveOrderByTableId(UUID tableId) {
    return orderJpaRepository
        .findByTableIdAndStatus(tableId, OrderStatus.OPEN)
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames);
  }

  @Override
  public List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId) {
    return orderJpaRepository.findAllByTableIdOrderByOpenedAtDesc(tableId).stream()
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames)
        .toList();
  }

  @Override
  public List<Order> findAllByTableIdAndStatusAndOpenedAtBetween(
      UUID tableId,
      OrderStatus status,
      java.time.ZonedDateTime start,
      java.time.ZonedDateTime end) {
    return orderJpaRepository.findAllFiltered(tableId, status, start, end).stream()
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames)
        .toList();
  }

  @Override
  public void save(Order order) {
    orderJpaRepository.save(OrderEntity.fromDomain(order));

    // Publish Domain Events
    order.getEvents().forEach(eventPublisher::publishEvent);
    order.clearEvents();
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status) {
    return orderJpaRepository.findAllByRestaurantIdAndStatus(restaurantId, status).stream()
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames)
        .toList();
  }

  @Override
  public List<Order> findAllByRestaurantIdAndStatusAndClosedAtAfter(
      UUID restaurantId, OrderStatus status, java.time.ZonedDateTime threshold) {
    return orderJpaRepository
        .findAllByRestaurantIdAndStatusAndClosedAtAfter(restaurantId, status, threshold)
        .stream()
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames)
        .toList();
  }

  @Override
  public Optional<Order> findByTicketId(UUID ticketId) {
    return orderJpaRepository
        .findByTicketId(ticketId)
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames);
  }

  @Override
  public Optional<Order> findByTicketItemId(UUID itemId) {
    return orderJpaRepository
        .findByTicketItemId(itemId)
        .map(OrderEntity::toDomain)
        .map(this::fillItemNames);
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
