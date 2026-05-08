package dev.thiagooliveira.tablesplit.domain.common;

public interface EventPublisher {

  void publishEvent(DomainEvent event);
}
