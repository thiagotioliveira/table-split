package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public record FeedbackSubmittedEvent(
    UUID accountId, UUID restaurantId, UUID orderId, UUID feedbackId) implements DomainEvent {

  @Override
  public UUID getAccountId() {
    return accountId;
  }
}
