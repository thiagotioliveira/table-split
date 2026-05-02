package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.api.FeedbacksApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.spec.v1.model.*;
import java.time.ZonedDateTime;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
public class FeedbackApiController implements FeedbacksApi {

  private final GetFeedbackOverview getFeedbackOverview;
  private final dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository;
  private final org.springframework.context.MessageSource messageSource;
  private final dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount
      getFeedbackUnreadCount;

  public FeedbackApiController(
      GetFeedbackOverview getFeedbackOverview,
      dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository,
      org.springframework.context.MessageSource messageSource,
      dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount
          getFeedbackUnreadCount) {
    this.getFeedbackOverview = getFeedbackOverview;
    this.orderRepository = orderRepository;
    this.messageSource = messageSource;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
  }

  @org.springframework.web.bind.annotation.GetMapping("/feedbacks/count/unread")
  public ResponseEntity<Long> getUnreadCount() {
    AccountContext context = getContext();
    return ResponseEntity.ok(getFeedbackUnreadCount.execute(context.getRestaurant().getId()));
  }

  @Override
  public ResponseEntity<FeedbackOverviewResponse> getFeedbacks(Integer days) {
    AccountContext context = getContext();
    ZonedDateTime since = ZonedDateTime.now().minusDays(days != null ? days : 30);

    GetFeedbackOverview.Overview overview =
        getFeedbackOverview.execute(context.getRestaurant().getId(), since);

    FeedbackOverviewResponse response = new FeedbackOverviewResponse();
    response.setFeedbacks(mapFeedbacks(overview.feedbacks(), context.getUser().getLanguage()));
    response.setStats(mapStats(overview));
    response.setDistribution(mapDistribution(overview.distribution()));
    response.setTopRatedItems(mapItemRankings(overview.topRated()));
    response.setNeedAttentionItems(mapItemRankings(overview.needAttention()));

    return ResponseEntity.ok(response);
  }

  private List<OrderFeedbackResponse> mapFeedbacks(
      List<OrderFeedback> feedbacks,
      dev.thiagooliveira.tablesplit.domain.common.Language language) {
    return feedbacks.stream()
        .map(
            f -> {
              OrderFeedbackResponse res = new OrderFeedbackResponse();
              res.setId(f.getId());
              res.setOrderId(f.getOrderId());
              res.setCustomerId(f.getCustomerId());
              res.setRating(f.getRating());
              res.setComment(f.getComment());
              res.setCreatedAt(f.getCreatedAt().toOffsetDateTime());
              res.setTimeAgo(
                  dev.thiagooliveira.tablesplit.infrastructure.utils.TimeUtils.timeAgo(
                      f.getCreatedAt(), messageSource, language));

              orderRepository
                  .findById(f.getOrderId())
                  .ifPresent(
                      order -> {
                        res.setOrderShortId(order.getId().toString().substring(0, 4).toUpperCase());
                        res.setCustomerName(order.getCustomerName(f.getCustomerId()));

                        List<FeedbackItemResponse> items =
                            order.getItems().stream()
                                .filter(
                                    i ->
                                        f.getCustomerId().equals(i.getCustomerId())
                                            && i.getRating() != null)
                                .map(
                                    i -> {
                                      FeedbackItemResponse itemRes = new FeedbackItemResponse();
                                      itemRes.setName(i.getName().get(language));
                                      itemRes.setRating(i.getRating());
                                      return itemRes;
                                    })
                                .toList();
                        res.setItems(items);
                      });

              return res;
            })
        .toList();
  }

  private FeedbackStats mapStats(GetFeedbackOverview.Overview overview) {
    FeedbackStats stats = new FeedbackStats();
    double avg =
        overview.feedbacks().stream().mapToInt(OrderFeedback::getRating).average().orElse(0.0);
    stats.setAverageRating(avg);
    stats.setTotalFeedbacks(overview.feedbacks().size());
    stats.setPositiveCount(
        (int) overview.feedbacks().stream().filter(f -> f.getRating() >= 4).count());
    stats.setNegativeCount(
        (int) overview.feedbacks().stream().filter(f -> f.getRating() <= 2).count());

    long itemsRated =
        overview.feedbacks().stream()
            .map(f -> f.getOrderId())
            .distinct()
            .map(orderRepository::findById)
            .filter(Optional::isPresent)
            .flatMap(o -> o.get().getItems().stream())
            .filter(i -> i.getRating() != null)
            .count();

    stats.setTotalItemsRated((int) itemsRated);
    stats.setRatingTrend(0.3); // Dummy trend
    stats.setTotalTrend(12.0); // Dummy trend
    return stats;
  }

  private RatingDistribution mapDistribution(Map<Integer, Long> dist) {
    RatingDistribution res = new RatingDistribution();
    res.setR5(dist.getOrDefault(5, 0L).intValue());
    res.setR4(dist.getOrDefault(4, 0L).intValue());
    res.setR3(dist.getOrDefault(3, 0L).intValue());
    res.setR2(dist.getOrDefault(2, 0L).intValue());
    res.setR1(dist.getOrDefault(1, 0L).intValue());
    res.setR0(dist.getOrDefault(0, 0L).intValue());
    return res;
  }

  private List<ItemRatingResponse> mapItemRankings(
      List<dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository.ItemRating> ratings) {
    return ratings.stream()
        .map(
            r -> {
              ItemRatingResponse res = new ItemRatingResponse();
              res.setItemId(r.itemId());
              res.setName(r.name());
              res.setAverageRating(r.averageRating());
              res.setReviewCount(r.reviewCount().intValue());
              res.setImageUrl(r.imageUrl());
              return res;
            })
        .toList();
  }

  protected AccountContext getContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (AccountContext) auth.getPrincipal();
  }
}
