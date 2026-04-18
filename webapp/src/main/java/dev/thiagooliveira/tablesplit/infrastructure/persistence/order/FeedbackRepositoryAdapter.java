package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.application.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRepositoryAdapter implements FeedbackRepository {

  private final OrderFeedbackJpaRepository orderFeedbackJpaRepository;
  private final TicketItemJpaRepository ticketItemJpaRepository;

  public FeedbackRepositoryAdapter(
      OrderFeedbackJpaRepository orderFeedbackJpaRepository,
      TicketItemJpaRepository ticketItemJpaRepository) {
    this.orderFeedbackJpaRepository = orderFeedbackJpaRepository;
    this.ticketItemJpaRepository = ticketItemJpaRepository;
  }

  @Override
  public void save(OrderFeedback feedback) {
    orderFeedbackJpaRepository.save(OrderFeedbackEntity.fromDomain(feedback));
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
