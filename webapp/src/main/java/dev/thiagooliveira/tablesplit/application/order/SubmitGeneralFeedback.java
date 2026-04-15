package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;

public class SubmitGeneralFeedback {

  private final FeedbackRepository feedbackRepository;

  public SubmitGeneralFeedback(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public void execute(UUID orderId, UUID customerId, Integer rating, String comment) {
    OrderFeedback feedback =
        new OrderFeedback(UUID.randomUUID(), orderId, customerId, rating, comment);
    feedbackRepository.save(feedback);
  }
}
