package dev.thiagooliveira.tablesplit.infrastructure.publisher;

import dev.thiagooliveira.tablesplit.domain.common.DomainEvent;
import dev.thiagooliveira.tablesplit.domain.common.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisherAdapter implements DomainEventPublisher {

  private final org.springframework.context.ApplicationEventPublisher applicationEventPublisher;

  public DomainEventPublisherAdapter(
      org.springframework.context.ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishEvent(DomainEvent event) {
    this.applicationEventPublisher.publishEvent(event);
  }
}
