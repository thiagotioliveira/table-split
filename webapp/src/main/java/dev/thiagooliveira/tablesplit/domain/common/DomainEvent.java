package dev.thiagooliveira.tablesplit.domain.common;

import java.util.UUID;

public interface DomainEvent {
  UUID getAccountId();
}
