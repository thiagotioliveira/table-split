package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRepositoryAdapter implements FeedbackRepository {

  private final OrderFeedbackJpaRepository orderFeedbackJpaRepository;
  private final TicketItemJpaRepository ticketItemJpaRepository;
  private final OrderJpaRepository orderJpaRepository;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository
      itemJpaRepository;
  private final OrderEntityMapper mapper;

  public FeedbackRepositoryAdapter(
      OrderFeedbackJpaRepository orderFeedbackJpaRepository,
      TicketItemJpaRepository ticketItemJpaRepository,
      OrderJpaRepository orderJpaRepository,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository
          itemJpaRepository,
      OrderEntityMapper mapper) {
    this.orderFeedbackJpaRepository = orderFeedbackJpaRepository;
    this.ticketItemJpaRepository = ticketItemJpaRepository;
    this.orderJpaRepository = orderJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
    this.mapper = mapper;
  }

  @Override
  public void save(OrderFeedback feedback) {
    OrderFeedbackEntity entity = mapper.feedbackToEntity(feedback);
    entity.setOrder(orderJpaRepository.getReferenceById(feedback.getOrderId()));
    orderFeedbackJpaRepository.save(entity);
  }

  @Override
  public void saveItemRating(UUID itemId, Integer rating) {
    ticketItemJpaRepository.updateRating(itemId, rating);
  }

  @Override
  public boolean hasFeedback(UUID orderId, UUID customerId) {
    return orderFeedbackJpaRepository.existsByOrder_IdAndCustomerId(orderId, customerId);
  }

  @Override
  public java.util.List<OrderFeedback> findAll(UUID restaurantId, java.time.ZonedDateTime since) {
    return orderFeedbackJpaRepository
        .findAllByOrder_RestaurantIdAndCreatedAtAfterOrderByCreatedAtDesc(restaurantId, since)
        .stream()
        .map(mapper::feedbackToDomain)
        .toList();
  }

  @Override
  public java.util.Map<Integer, Long> getRatingDistribution(
      UUID restaurantId, java.time.ZonedDateTime since) {
    java.util.List<Object[]> results =
        orderFeedbackJpaRepository.getRatingDistribution(restaurantId, since);
    java.util.Map<Integer, Long> distribution = new java.util.HashMap<>();
    for (Object[] result : results) {
      distribution.put((Integer) result[0], (Long) result[1]);
    }
    return distribution;
  }

  @Override
  public java.util.List<ItemRating> getTopRatedItems(
      UUID restaurantId, java.time.ZonedDateTime since, int limit) {
    return ticketItemJpaRepository
        .findTopRatedItems(
            restaurantId, since, org.springframework.data.domain.PageRequest.of(0, limit))
        .getContent()
        .stream()
        .map(
            obj -> {
              UUID itemId = (UUID) obj[0];
              var item = itemJpaRepository.findById(itemId);
              String name =
                  item.map(
                          i -> {
                            if (i.getName() == null) return "Item";
                            var translations = i.getName().getTranslations();
                            return translations.getOrDefault(
                                dev.thiagooliveira.tablesplit.domain.common.Language.PT,
                                translations.getOrDefault(
                                    dev.thiagooliveira.tablesplit.domain.common.Language.EN,
                                    "Item"));
                          })
                      .orElse("Item");
              String imageUrl =
                  item.flatMap(
                          i ->
                              i.getImages().stream()
                                  .filter(
                                      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu
                                              .ItemImageEntity
                                          ::isMain)
                                  .map(
                                      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu
                                              .ItemImageEntity
                                          ::getName)
                                  .findFirst()
                                  .or(
                                      () ->
                                          i.getImages().stream()
                                              .map(
                                                  dev.thiagooliveira.tablesplit.infrastructure
                                                          .persistence.menu.ItemImageEntity
                                                      ::getName)
                                              .findFirst()))
                      .orElse(null);
              return new ItemRating(itemId, name, (Double) obj[1], (Long) obj[2], imageUrl);
            })
        .toList();
  }

  @Override
  public java.util.List<ItemRating> getNeedAttentionItems(
      UUID restaurantId, java.time.ZonedDateTime since, int limit) {
    return ticketItemJpaRepository
        .findNeedAttentionItems(
            restaurantId, since, org.springframework.data.domain.PageRequest.of(0, limit))
        .getContent()
        .stream()
        .map(
            obj -> {
              UUID itemId = (UUID) obj[0];
              var item = itemJpaRepository.findById(itemId);
              String name =
                  item.map(
                          i -> {
                            if (i.getName() == null) return "Item";
                            var translations = i.getName().getTranslations();
                            return translations.getOrDefault(
                                dev.thiagooliveira.tablesplit.domain.common.Language.PT,
                                translations.getOrDefault(
                                    dev.thiagooliveira.tablesplit.domain.common.Language.EN,
                                    "Item"));
                          })
                      .orElse("Item");
              String imageUrl =
                  item.flatMap(
                          i ->
                              i.getImages().stream()
                                  .filter(
                                      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu
                                              .ItemImageEntity
                                          ::isMain)
                                  .map(
                                      dev.thiagooliveira.tablesplit.infrastructure.persistence.menu
                                              .ItemImageEntity
                                          ::getName)
                                  .findFirst()
                                  .or(
                                      () ->
                                          i.getImages().stream()
                                              .map(
                                                  dev.thiagooliveira.tablesplit.infrastructure
                                                          .persistence.menu.ItemImageEntity
                                                      ::getName)
                                              .findFirst()))
                      .orElse(null);
              return new ItemRating(itemId, name, (Double) obj[1], (Long) obj[2], imageUrl);
            })
        .toList();
  }

  @Override
  public long countUnread(UUID restaurantId) {
    return orderFeedbackJpaRepository.countByOrder_RestaurantIdAndReadFalse(restaurantId);
  }

  @Override
  @org.springframework.transaction.annotation.Transactional
  public void markAsRead(UUID restaurantId) {
    orderFeedbackJpaRepository.markAllAsReadByRestaurantId(restaurantId);
  }
}
