package dev.thiagooliveira.tablesplit.infrastructure.order.persistence;

import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemImageEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRepositoryAdapter implements FeedbackRepository {

  private final OrderFeedbackJpaRepository orderFeedbackJpaRepository;
  private final TicketItemJpaRepository ticketItemJpaRepository;
  private final OrderJpaRepository orderJpaRepository;
  private final ItemJpaRepository itemJpaRepository;
  private final OrderEntityMapper mapper;

  public FeedbackRepositoryAdapter(
      OrderFeedbackJpaRepository orderFeedbackJpaRepository,
      TicketItemJpaRepository ticketItemJpaRepository,
      OrderJpaRepository orderJpaRepository,
      ItemJpaRepository itemJpaRepository,
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
  public dev.thiagooliveira.tablesplit.domain.common.Pagination<OrderFeedback> findAll(
      UUID restaurantId,
      java.time.ZonedDateTime since,
      Integer rating,
      String search,
      int page,
      int size) {
    org.springframework.data.jpa.domain.Specification<OrderFeedbackEntity> spec =
        org.springframework.data.jpa.domain.Specification.where(
            (root, query, cb) -> cb.equal(root.get("order").get("restaurantId"), restaurantId));

    if (since != null) {
      spec = spec.and((root, query, cb) -> cb.greaterThan(root.get("createdAt"), since));
    }

    if (rating != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("rating"), rating));
    }

    if (search != null && !search.isBlank()) {
      String pattern = "%" + search.toLowerCase() + "%";
      spec =
          spec.and(
              (root, query, cb) -> {
                var orderJoin = root.join("order");
                var customersJoin = orderJoin.join("customers");
                return cb.or(
                    cb.like(cb.lower(customersJoin.get("name")), pattern),
                    cb.like(cb.lower(root.get("comment")), pattern),
                    cb.like(cb.lower(orderJoin.get("id").as(String.class)), pattern));
              });
    }

    // Force JOIN FETCH for order, tickets and items to avoid N+1
    // Note: Specification doesn't easily support JOIN FETCH with pagination in some Hibernate
    // versions,
    // but findFeedbacks already uses it. For dynamic specs, we rely on the repository's default
    // behavior or custom query if needed.
    // However, findFeedbacks is hardcoded. Let's use the specification with the standard findAll.

    org.springframework.data.domain.Page<OrderFeedbackEntity> entityPage =
        orderFeedbackJpaRepository.findAll(
            spec,
            org.springframework.data.domain.PageRequest.of(
                page, size, org.springframework.data.domain.Sort.by("createdAt").descending()));

    return new dev.thiagooliveira.tablesplit.domain.common.Pagination<>(
        entityPage.getContent().stream().map(mapper::feedbackToDomain).toList(),
        entityPage.getNumber(),
        entityPage.getTotalPages(),
        entityPage.getTotalElements(),
        entityPage.getSize(),
        entityPage.hasNext());
  }

  @Override
  public java.util.List<OrderFeedback> findAllUnpaginated(
      UUID restaurantId, java.time.ZonedDateTime since) {
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
                                  .filter(ItemImageEntity::isMain)
                                  .map(ItemImageEntity::getName)
                                  .findFirst()
                                  .or(
                                      () ->
                                          i.getImages().stream()
                                              .map(ItemImageEntity::getName)
                                              .findFirst()))
                      .orElse(null);
              return new ItemRating(
                  itemId,
                  name,
                  ((Number) obj[1]).doubleValue(),
                  ((Number) obj[2]).longValue(),
                  imageUrl);
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
                                  .filter(ItemImageEntity::isMain)
                                  .map(ItemImageEntity::getName)
                                  .findFirst()
                                  .or(
                                      () ->
                                          i.getImages().stream()
                                              .map(ItemImageEntity::getName)
                                              .findFirst()))
                      .orElse(null);
              return new ItemRating(
                  itemId,
                  name,
                  ((Number) obj[1]).doubleValue(),
                  ((Number) obj[2]).longValue(),
                  imageUrl);
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
