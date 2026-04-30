package dev.thiagooliveira.tablesplit.infrastructure.config.account;

import dev.thiagooliveira.tablesplit.application.account.GetUser;
import dev.thiagooliveira.tablesplit.application.account.UpdatePassword;
import dev.thiagooliveira.tablesplit.application.account.UpdateUser;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

  @Bean
  public GetUser getUser(UserRepository userRepository) {
    return new GetUser(userRepository);
  }

  @Bean
  public UpdateUser updateUser(UserRepository userRepository) {
    return new UpdateUser(userRepository);
  }

  @Bean
  public UpdatePassword updatePassword(UserRepository userRepository) {
    return new UpdatePassword(userRepository);
  }
}
