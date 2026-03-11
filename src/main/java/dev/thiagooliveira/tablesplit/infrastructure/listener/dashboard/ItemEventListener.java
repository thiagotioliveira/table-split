package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.DashboardRepository;
import dev.thiagooliveira.tablesplit.domain.dashboard.v1.ItemAttributes;
import dev.thiagooliveira.tablesplit.domain.event.ItemCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemEventListener {
  private final DashboardRepository dashboardRepository;

  public ItemEventListener(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  @EventListener
  public void on(ItemCreatedEvent event) {
    var dashboards = this.dashboardRepository.findByAccountId(event.getAccountId());
    dashboards.forEach(
        d -> {
          d.getAttributes().getItems().removeLast();
          d.getAttributes()
              .getItems()
              .getList()
              .addFirst(
                  new ItemAttributes.Item(
                      event.getItemId(),
                      event.getDetails().getCategoryId(),
                      event.getDetails().getCategoryName(),
                      event.getDetails().getName(),
                      event.getDetails().getImageUrl(),
                      event.getDetails().getPrice()));
          d.getAttributes()
              .setItems(
                  new ItemAttributes(
                      d.getAttributes().getItems().getList(),
                      event.getDetails().getTotal(),
                      event.getDetails().getTotalActive(),
                      event.getDetails().getTotalInactive()));
          this.dashboardRepository.save(d);
        });
  }

  @EventListener
  public void on(ItemDeletedEvent event) {
    var dashboards = this.dashboardRepository.findByAccountId(event.getAccountId());
    dashboards.forEach(
        d -> {
          var items = d.getAttributes().getItems().getList();
          var toRemove = items.stream().filter(i -> i.getId().equals(event.getItemId())).toList();
          items.removeAll(toRemove);
          d.getAttributes()
              .setItems(
                  new ItemAttributes(
                      items,
                      event.getDetails().getTotal(),
                      event.getDetails().getTotalActive(),
                      event.getDetails().getTotalInactive()));
          this.dashboardRepository.save(d);
        });
  }

  @EventListener
  public void on(ItemUpdatedEvent event) {
    var dashboards = this.dashboardRepository.findByAccountId(event.getAccountId());
    dashboards.forEach(
        d -> {
          var items = d.getAttributes().getItems().getList();
          items.stream()
              .filter(i -> i.getId().equals(event.getItemId()))
              .forEach(
                  i -> {
                    i.setCategoryName(event.getDetails().getCategoryName());
                    i.setImageUrl(event.getDetails().getImageUrl());
                    i.setPrice(event.getDetails().getPrice());
                    i.setName(event.getDetails().getName());
                  });
          d.getAttributes()
              .setItems(
                  new ItemAttributes(
                      items,
                      event.getDetails().getTotal(),
                      event.getDetails().getTotalActive(),
                      event.getDetails().getTotalInactive()));
          this.dashboardRepository.save(d);
        });
  }
}
