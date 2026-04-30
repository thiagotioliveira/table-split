package dev.thiagooliveira.tablesplit.domain.common;

import dev.thiagooliveira.tablesplit.domain.event.DomainEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

  private final List<DomainEvent> events = new ArrayList<>();

  protected void registerEvent(DomainEvent event) {
    this.events.add(event);
  }

  public Collection<DomainEvent> getDomainEvents() {
    return Collections.unmodifiableList(events);
  }

  public void clearEvents() {
    this.events.clear();
  }
}
