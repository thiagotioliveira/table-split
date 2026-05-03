package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import java.util.UUID;

public class GetFeedbackUnreadCount {
  private final FeedbackRepository feedbackRepository;

  public GetFeedbackUnreadCount(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public long execute(UUID restaurantId) {
    return feedbackRepository.countUnread(restaurantId);
  }
}
