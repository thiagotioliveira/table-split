package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderFeedbackJpaRepository extends JpaRepository<OrderFeedbackEntity, UUID> {
  boolean existsByOrder_IdAndCustomerId(UUID orderId, UUID customerId);
}
