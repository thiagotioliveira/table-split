package dev.thiagooliveira.tablesplit.application;

import dev.thiagooliveira.tablesplit.domain.event.DomainEvent;

public interface EventPublisher {

  void publishEvent(DomainEvent event);
}
