package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.application.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRepositoryAdapter implements FeedbackRepository {

  private final OrderFeedbackJpaRepository orderFeedbackJpaRepository;
  private final TicketItemJpaRepository ticketItemJpaRepository;
  private final OrderJpaRepository orderJpaRepository;

  public FeedbackRepositoryAdapter(
      OrderFeedbackJpaRepository orderFeedbackJpaRepository,
      TicketItemJpaRepository ticketItemJpaRepository,
      OrderJpaRepository orderJpaRepository) {
    this.orderFeedbackJpaRepository = orderFeedbackJpaRepository;
    this.ticketItemJpaRepository = ticketItemJpaRepository;
    this.orderJpaRepository = orderJpaRepository;
  }

  @Override
  public void save(OrderFeedback feedback) {
    OrderFeedbackEntity entity = OrderFeedbackEntity.fromDomain(feedback);
    entity.setOrder(orderJpaRepository.getReferenceById(feedback.getOrderId()));
    orderFeedbackJpaRepository.save(entity);
  }

  @Override
  public void saveItemRating(UUID itemId, Integer rating) {
    ticketItemJpaRepository.updateRating(itemId, rating);
  }

  @Override
  public boolean hasFeedback(UUID orderId, UUID customerId) {
    return orderFeedbackJpaRepository.existsByOrder_IdAndCustomerId(orderId, customerId);
  }
}
