package dev.thiagooliveira.tablesplit.domain.event;

import java.util.UUID;

public interface DomainEvent {
  UUID getAccountId();
}
