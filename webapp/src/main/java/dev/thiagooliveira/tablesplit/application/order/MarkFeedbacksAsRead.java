package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MarkFeedbacksAsRead {
  private final FeedbackRepository feedbackRepository;

  public MarkFeedbacksAsRead(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public void execute(UUID restaurantId) {
    feedbackRepository.markAsRead(restaurantId);
  }
}
