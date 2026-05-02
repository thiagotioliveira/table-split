package dev.thiagooliveira.tablesplit.domain.order;

import java.util.UUID;

public interface FeedbackRepository {
  void save(OrderFeedback feedback);

  void saveItemRating(UUID itemId, Integer rating);

  boolean hasFeedback(UUID orderId, UUID customerId);

  java.util.List<OrderFeedback> findAll(UUID restaurantId, java.time.ZonedDateTime since);

  java.util.Map<Integer, Long> getRatingDistribution(
      UUID restaurantId, java.time.ZonedDateTime since);

  java.util.List<ItemRating> getTopRatedItems(
      UUID restaurantId, java.time.ZonedDateTime since, int limit);

  java.util.List<ItemRating> getNeedAttentionItems(
      UUID restaurantId, java.time.ZonedDateTime since, int limit);

  long countUnread(UUID restaurantId);

  void markAsRead(UUID restaurantId);

  public record ItemRating(
      UUID itemId, String name, Double averageRating, Long reviewCount, String imageUrl) {}
}
