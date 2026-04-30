package dev.thiagooliveira.tablesplit.infrastructure.config.notification;

import dev.thiagooliveira.tablesplit.application.notification.*;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscriptionRepository;
import dev.thiagooliveira.tablesplit.domain.notification.WaiterCallRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

  @Bean
  public Subscribe subscribe(PushSubscriptionRepository repository) {
    return new Subscribe(repository);
  }

  @Bean
  public Unsubscribe unsubscribe(PushSubscriptionRepository repository) {
    return new Unsubscribe(repository);
  }

  @Bean
  public GetPreferences getPreferences(PushSubscriptionRepository repository) {
    return new GetPreferences(repository);
  }

  @Bean
  public UpdatePreferences updatePreferences(PushSubscriptionRepository repository) {
    return new UpdatePreferences(repository);
  }

  @Bean
  public Broadcaster broadcaster(PushSubscriptionRepository repository, PushSender sender) {
    return new Broadcaster(repository, sender);
  }

  @Bean
  public RegisterWaiterCall registerWaiterCall(
      WaiterCallRepository repository, Broadcaster broadcaster) {
    return new RegisterWaiterCall(repository, broadcaster);
  }

  @Bean
  public ListActiveWaiterCalls listActiveWaiterCalls(WaiterCallRepository repository) {
    return new ListActiveWaiterCalls(repository);
  }

  @Bean
  public DismissWaiterCall dismissWaiterCall(WaiterCallRepository repository) {
    return new DismissWaiterCall(repository);
  }
}
