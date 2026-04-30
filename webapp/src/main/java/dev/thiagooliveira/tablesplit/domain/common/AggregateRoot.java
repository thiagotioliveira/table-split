package dev.thiagooliveira.tablesplit.domain.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

  private final List<Object> events = new ArrayList<>();

  protected void registerEvent(Object event) {
    this.events.add(event);
  }

  public Collection<Object> getEvents() {
    return Collections.unmodifiableList(events);
  }

  public void clearEvents() {
    this.events.clear();
  }
}
