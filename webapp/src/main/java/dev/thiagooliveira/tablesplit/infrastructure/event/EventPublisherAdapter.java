package dev.thiagooliveira.tablesplit.infrastructure.event;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherAdapter implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public EventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishEvent(DomainEvent event) {
    this.applicationEventPublisher.publishEvent(event);
  }
}
