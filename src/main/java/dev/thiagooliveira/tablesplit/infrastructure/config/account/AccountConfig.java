package dev.thiagooliveira.tablesplit.infrastructure.config.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.GetUser;
import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

  @Bean
  public CreateAccount createAccount(
      EventPublisher eventPublisher,
      AccountRepository accountRepository,
      UserRepository userRepository) {
    return new CreateAccount(eventPublisher, accountRepository, userRepository);
  }

  @Bean
  public GetUser getUser(UserRepository userRepository) {
    return new GetUser(userRepository);
  }
}
