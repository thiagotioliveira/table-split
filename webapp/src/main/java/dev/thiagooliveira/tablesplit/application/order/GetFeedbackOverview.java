package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetFeedbackOverview {

  private final FeedbackRepository feedbackRepository;

  public GetFeedbackOverview(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public Overview execute(UUID restaurantId, ZonedDateTime since) {
    List<OrderFeedback> feedbacks = feedbackRepository.findAll(restaurantId, since);
    Map<Integer, Long> distribution = feedbackRepository.getRatingDistribution(restaurantId, since);
    List<FeedbackRepository.ItemRating> topRated =
        feedbackRepository.getTopRatedItems(restaurantId, since, 5);
    List<FeedbackRepository.ItemRating> needAttention =
        feedbackRepository.getNeedAttentionItems(restaurantId, since, 5);

    return new Overview(feedbacks, distribution, topRated, needAttention);
  }

  public record Overview(
      List<OrderFeedback> feedbacks,
      Map<Integer, Long> distribution,
      List<FeedbackRepository.ItemRating> topRated,
      List<FeedbackRepository.ItemRating> needAttention) {}
}
