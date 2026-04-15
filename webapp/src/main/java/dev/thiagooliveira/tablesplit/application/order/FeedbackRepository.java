package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;

public interface FeedbackRepository {
  void save(OrderFeedback feedback);

  void saveItemRating(UUID itemId, Integer rating);

  boolean hasFeedback(UUID orderId, UUID customerId);
}
