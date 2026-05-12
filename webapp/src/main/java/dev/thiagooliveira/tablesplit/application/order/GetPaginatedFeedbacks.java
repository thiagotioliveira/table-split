package dev.thiagooliveira.tablesplit.application.order;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Pagination;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetPaginatedFeedbacks {

  private final FeedbackRepository feedbackRepository;
  private final ItemRepository itemRepository;

  public GetPaginatedFeedbacks(
      FeedbackRepository feedbackRepository, ItemRepository itemRepository) {
    this.feedbackRepository = feedbackRepository;
    this.itemRepository = itemRepository;
  }

  public Pagination<OrderFeedback> execute(
      UUID restaurantId,
      ZonedDateTime since,
      Integer rating,
      String search,
      int page,
      int size,
      Language language) {
    Pagination<OrderFeedback> pagination =
        feedbackRepository.findAll(restaurantId, since, rating, search, page, size);

    List<UUID> itemIds =
        pagination.items().stream()
            .flatMap(f -> f.getItems().stream())
            .map(OrderFeedback.FeedbackItem::itemId)
            .distinct()
            .toList();

    if (!itemIds.isEmpty()) {
      // Get names for the requested language across domains
      Map<UUID, String> itemNames =
          itemRepository.findAll(restaurantId, List.of(language)).stream()
              .collect(
                  Collectors.toMap(
                      Item::getId, i -> i.getName().getOrDefault(language, "Item"), (a, b) -> a));

      // Enrich feedback items with names
      pagination
          .items()
          .forEach(
              f -> {
                List<OrderFeedback.FeedbackItem> enrichedItems =
                    f.getItems().stream()
                        .map(
                            i ->
                                new OrderFeedback.FeedbackItem(
                                    i.itemId(),
                                    itemNames.getOrDefault(i.itemId(), "Item"),
                                    i.rating()))
                        .toList();
                f.setItems(enrichedItems);
              });
    }

    return pagination;
  }
}
