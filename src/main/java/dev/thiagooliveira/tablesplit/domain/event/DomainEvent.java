package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public interface DomainEvent<T> {
  UUID getAccountId();

  T getDetails();
}
