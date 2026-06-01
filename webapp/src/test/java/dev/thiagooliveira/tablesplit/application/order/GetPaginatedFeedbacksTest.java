package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Pagination;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderFeedback;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetPaginatedFeedbacksTest {

  private FeedbackRepository feedbackRepository;
  private ItemRepository itemRepository;
  private GetPaginatedFeedbacks getPaginatedFeedbacks;

  @BeforeEach
  void setUp() {
    feedbackRepository = mock(FeedbackRepository.class);
    itemRepository = mock(ItemRepository.class);
    getPaginatedFeedbacks = new GetPaginatedFeedbacks(feedbackRepository, itemRepository);
  }

  @Test
  void shouldRetrieveAndEnrichFeedbacksSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();
    ZonedDateTime since = ZonedDateTime.now();
    Language lang = Language.PT;

    OrderFeedback feedback =
        new OrderFeedback(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, "Good");
    OrderFeedback.FeedbackItem fbItem = new OrderFeedback.FeedbackItem(itemId, null, 5);
    feedback.setItems(new ArrayList<>(List.of(fbItem)));

    Pagination<OrderFeedback> pagination = new Pagination<>(List.of(feedback), 1, 1, 1, 10, false);

    when(feedbackRepository.findAll(restaurantId, since, 5, "search", 1, 10))
        .thenReturn(pagination);

    Item menuItem = new Item();
    menuItem.setId(itemId);
    menuItem.setName(Map.of(Language.PT, "Burger"));

    when(itemRepository.findAll(restaurantId, List.of(lang))).thenReturn(List.of(menuItem));

    Pagination<OrderFeedback> result =
        getPaginatedFeedbacks.execute(restaurantId, since, 5, "search", 1, 10, lang);

    assertNotNull(result);
    assertEquals(1, result.items().size());
    OrderFeedback.FeedbackItem enriched = result.items().get(0).getItems().get(0);
    assertEquals("Burger", enriched.name());
  }
}
