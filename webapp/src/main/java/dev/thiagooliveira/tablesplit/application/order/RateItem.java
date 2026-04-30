package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import java.util.UUID;

public class RateItem {

  private final FeedbackRepository feedbackRepository;

  public RateItem(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public void execute(UUID itemId, Integer rating) {
    if (rating < 1 || rating > 5) {
      throw new IllegalArgumentException("Rating must be between 1 and 5");
    }
    feedbackRepository.saveItemRating(itemId, rating);
  }

  public boolean hasFeedback(UUID orderId, UUID customerId) {
    return feedbackRepository.hasFeedback(orderId, customerId);
  }
}
