package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.FeedbacksApi;
import dev.thiagooliveira.tablesplit.infrastructure.order.api.spec.v1.model.*;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
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
  private final dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository feedbackRepository;
  private final org.springframework.context.MessageSource messageSource;
  private final dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount
      getFeedbackUnreadCount;
  private final dev.thiagooliveira.tablesplit.application.order.GetPaginatedFeedbacks
      getPaginatedFeedbacks;

  public FeedbackApiController(
      GetFeedbackOverview getFeedbackOverview,
      dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository,
      dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository feedbackRepository,
      org.springframework.context.MessageSource messageSource,
      dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount getFeedbackUnreadCount,
      dev.thiagooliveira.tablesplit.application.order.GetPaginatedFeedbacks getPaginatedFeedbacks) {
    this.getFeedbackOverview = getFeedbackOverview;
    this.orderRepository = orderRepository;
    this.feedbackRepository = feedbackRepository;
    this.messageSource = messageSource;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
    this.getPaginatedFeedbacks = getPaginatedFeedbacks;
  }

  @org.springframework.web.bind.annotation.GetMapping("/feedbacks/count/unread")
  public ResponseEntity<Long> getUnreadCount() {
    AccountContext context = getContext();
    return ResponseEntity.ok(getFeedbackUnreadCount.execute(context.getRestaurant().getId()));
  }

  @Override
  public ResponseEntity<FeedbackOverviewResponse> getFeedbackOverview(Integer days) {
    AccountContext context = getContext();
    ZonedDateTime since = Time.nowZonedDateTime().minusDays(days != null ? days : 30);

    GetFeedbackOverview.Overview overview =
        getFeedbackOverview.execute(context.getRestaurant().getId(), since);

    FeedbackOverviewResponse response = new FeedbackOverviewResponse();
    response.setStats(mapStats(overview));
    response.setDistribution(mapDistribution(overview.distribution()));
    response.setTopRatedItems(mapItemRankings(overview.topRated()));
    response.setNeedAttentionItems(mapItemRankings(overview.needAttention()));

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<FeedbackListResponse> getFeedbacks(
      Integer days, Integer page, Integer size) {
    AccountContext context = getContext();
    ZonedDateTime since = Time.nowZonedDateTime().minusDays(days != null ? days : 30);

    dev.thiagooliveira.tablesplit.domain.common.Pagination<OrderFeedback> pagination =
        getPaginatedFeedbacks.execute(
            context.getRestaurant().getId(),
            since,
            page != null ? page : 0,
            size != null ? size : 10,
            context.getUser().getLanguage());

    FeedbackListResponse response = new FeedbackListResponse();
    response.setFeedbacks(mapFeedbacks(pagination.items(), context.getUser().getLanguage()));
    response.setPagination(mapPagination(pagination));

    return ResponseEntity.ok(response);
  }

  private PaginationResponse mapPagination(
      dev.thiagooliveira.tablesplit.domain.common.Pagination<OrderFeedback> pagination) {
    PaginationResponse res = new PaginationResponse();
    res.setCurrentPage(pagination.currentPage());
    res.setTotalPages(pagination.totalPages());
    res.setTotalElements((int) pagination.totalElements());
    res.setSize(pagination.size());
    res.setHasNext(pagination.hasNext());
    return res;
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

              // Optimized: Using the domain object which already contains order details from the
              // optimized repository query
              res.setOrderShortId(f.getOrderId().toString().substring(0, 4).toUpperCase());
              res.setCustomerName(f.getCustomerName());

              if (f.getItems() != null) {
                List<FeedbackItemResponse> items =
                    f.getItems().stream()
                        .map(
                            i -> {
                              FeedbackItemResponse itemRes = new FeedbackItemResponse();
                              itemRes.setName(i.name());
                              itemRes.setRating(i.rating());
                              return itemRes;
                            })
                        .toList();
                res.setItems(items);
              }

              return res;
            })
        .toList();
  }

  private FeedbackStats mapStats(GetFeedbackOverview.Overview overview) {
    FeedbackStats stats = new FeedbackStats();
    List<OrderFeedback> allFeedbacks = overview.allFeedbacksForStats();

    double avg = allFeedbacks.stream().mapToInt(OrderFeedback::getRating).average().orElse(0.0);
    stats.setAverageRating(avg);
    stats.setTotalFeedbacks(allFeedbacks.size());
    stats.setPositiveCount((int) allFeedbacks.stream().filter(f -> f.getRating() >= 4).count());
    stats.setNegativeCount((int) allFeedbacks.stream().filter(f -> f.getRating() <= 2).count());

    long itemsRated =
        allFeedbacks.stream()
            .flatMap(
                f -> f.getItems() != null ? f.getItems().stream() : java.util.stream.Stream.empty())
            .filter(i -> i.rating() != null)
            .count();

    stats.setTotalItemsRated((int) itemsRated);
    stats.setRatingTrend(0.0); // Trend calculation not implemented yet
    stats.setTotalTrend(0.0);
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
