package dev.thiagooliveira.tablesplit.domain.common;

public interface DomainEventPublisher {

  void publishEvent(DomainEvent event);
}
