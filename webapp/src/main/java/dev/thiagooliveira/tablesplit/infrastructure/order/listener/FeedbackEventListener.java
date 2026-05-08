package dev.thiagooliveira.tablesplit.infrastructure.order.listener;

import dev.thiagooliveira.tablesplit.domain.order.event.FeedbackSubmittedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.notification.SseService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventListener {
  private final SseService sseService;

  public FeedbackEventListener(SseService sseService) {
    this.sseService = sseService;
  }

  @EventListener
  public void handleFeedbackSubmitted(FeedbackSubmittedEvent event) {
    sseService.broadcast(
        event.restaurantId(),
        java.util.Map.of(
            "type",
            "FEEDBACK_SUBMITTED",
            "data",
            java.util.Map.of("feedbackId", event.feedbackId().toString())));
  }
}
