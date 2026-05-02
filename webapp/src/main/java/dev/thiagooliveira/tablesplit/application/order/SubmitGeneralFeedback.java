package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;

public class SubmitGeneralFeedback {

  private final FeedbackRepository feedbackRepository;
  private final dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;

  public SubmitGeneralFeedback(
      FeedbackRepository feedbackRepository,
      dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    this.feedbackRepository = feedbackRepository;
    this.orderRepository = orderRepository;
    this.eventPublisher = eventPublisher;
  }

  public void execute(UUID orderId, UUID customerId, Integer rating, String comment) {
    var order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    OrderFeedback feedback =
        new OrderFeedback(UUID.randomUUID(), orderId, customerId, rating, comment);
    feedbackRepository.save(feedback);

    eventPublisher.publishEvent(
        new dev.thiagooliveira.tablesplit.domain.event.FeedbackSubmittedEvent(
            order.getAccountId(), order.getRestaurantId(), orderId, feedback.getId()));
  }
}
