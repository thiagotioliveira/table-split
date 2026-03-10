package dev.thiagooliveira.tablesplit.infrastructure.listener.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.CreateDashboard;
import dev.thiagooliveira.tablesplit.application.dashboard.command.CreateDashboardCommand;
import dev.thiagooliveira.tablesplit.domain.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

  private final CreateDashboard createDashboard;

  public UserEventListener(CreateDashboard createDashboard) {
    this.createDashboard = createDashboard;
  }

  @EventListener
  public void on(UserCreatedEvent event) {
    this.createDashboard.execute(
        event.getAccountId(),
        event.getUserId(),
        new CreateDashboardCommand(event.getDetails().getFirstName()));
  }
}
