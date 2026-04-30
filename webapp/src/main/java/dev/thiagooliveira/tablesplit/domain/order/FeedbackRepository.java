package dev.thiagooliveira.tablesplit.domain.order;

import java.util.UUID;

public interface FeedbackRepository {
  void save(OrderFeedback feedback);

  void saveItemRating(UUID itemId, Integer rating);

  boolean hasFeedback(UUID orderId, UUID customerId);
}
