package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.application.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.domain.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;

  public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository) {
    this.orderJpaRepository = orderJpaRepository;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return orderJpaRepository.findById(id).map(OrderEntity::toDomain);
  }

  @Override
  public Optional<Order> findActiveOrderByTableId(UUID tableId) {
    return orderJpaRepository
        .findByTableIdAndStatus(tableId, OrderStatus.OPEN)
        .map(OrderEntity::toDomain);
  }

  @Override
  public List<Order> findAllByTableIdOrderByOpenedAtDesc(UUID tableId) {
    return orderJpaRepository.findAllByTableIdOrderByOpenedAtDesc(tableId).stream()
        .map(OrderEntity::toDomain)
        .toList();
  }

  @Override
  public void save(Order order) {
    orderJpaRepository.save(OrderEntity.fromDomain(order));
  }
}
