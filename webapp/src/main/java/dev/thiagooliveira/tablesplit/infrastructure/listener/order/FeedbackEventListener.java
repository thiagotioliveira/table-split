package dev.thiagooliveira.tablesplit.infrastructure.listener.order;

import dev.thiagooliveira.tablesplit.application.notification.SseService;
import dev.thiagooliveira.tablesplit.domain.event.FeedbackSubmittedEvent;
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
