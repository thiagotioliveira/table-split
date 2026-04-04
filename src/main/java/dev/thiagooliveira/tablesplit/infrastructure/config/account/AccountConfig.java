package dev.thiagooliveira.tablesplit.infrastructure.config.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.*;
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
  public CreateStaff createStaff(StaffRepository staffRepository) {
    return new CreateStaff(staffRepository);
  }

  @Bean
  public EditStaff editStaff(StaffRepository staffRepository) {
    return new EditStaff(staffRepository);
  }

  @Bean
  public DeleteStaff deleteStaff(StaffRepository staffRepository) {
    return new DeleteStaff(staffRepository);
  }

  @Bean
  public GetStaff getStaff(StaffRepository staffRepository) {
    return new GetStaff(staffRepository);
  }
}
