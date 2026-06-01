package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.*;
import dev.thiagooliveira.tablesplit.domain.order.event.FeedbackSubmittedEvent;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

class SubmitGeneralFeedbackTest {

  private FeedbackRepository feedbackRepository;
  private OrderRepository orderRepository;
  private ApplicationEventPublisher eventPublisher;
  private SubmitGeneralFeedback submitGeneralFeedback;

  @BeforeEach
  void setUp() {
    feedbackRepository = mock(FeedbackRepository.class);
    orderRepository = mock(OrderRepository.class);
    eventPublisher = mock(ApplicationEventPublisher.class);
    submitGeneralFeedback =
        new SubmitGeneralFeedback(feedbackRepository, orderRepository, eventPublisher);
  }

  @Test
  void shouldSubmitFeedbackSuccessfullyAndPublishEvent() {
    UUID orderId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();

    Order order = new Order(orderId, restaurantId, UUID.randomUUID(), 10);
    order.setAccountId(accountId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    submitGeneralFeedback.execute(orderId, customerId, 5, "Amazing experience");

    ArgumentCaptor<OrderFeedback> feedbackCaptor = ArgumentCaptor.forClass(OrderFeedback.class);
    verify(feedbackRepository).save(feedbackCaptor.capture());

    OrderFeedback saved = feedbackCaptor.getValue();
    assertNotNull(saved);
    assertEquals(orderId, saved.getOrderId());
    assertEquals(customerId, saved.getCustomerId());
    assertEquals(5, saved.getRating());
    assertEquals("Amazing experience", saved.getComment());

    ArgumentCaptor<FeedbackSubmittedEvent> eventCaptor =
        ArgumentCaptor.forClass(FeedbackSubmittedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    FeedbackSubmittedEvent event = eventCaptor.getValue();
    assertNotNull(event);
    assertEquals(accountId, event.accountId());
    assertEquals(restaurantId, event.restaurantId());
    assertEquals(orderId, event.orderId());
    assertEquals(saved.getId(), event.feedbackId());
  }

  @Test
  void shouldThrowExceptionWhenOrderNotFound() {
    UUID orderId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> submitGeneralFeedback.execute(orderId, customerId, 4, "Cool"));

    verify(feedbackRepository, never()).save(any());
    verify(eventPublisher, never()).publishEvent(any());
  }
}
