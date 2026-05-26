package dev.thiagooliveira.tablesplit.infrastructure.account.config;

import dev.thiagooliveira.tablesplit.application.account.*;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

  @Bean
  public PlanLimitValidator planLimitValidator(
      AccountRepository accountRepository,
      dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository restaurantRepository) {
    return new PlanLimitValidator(accountRepository, restaurantRepository);
  }

  @Bean
  public CreateAccount createAccount(
      AccountRepository accountRepository, UserRepository userRepository) {
    return new CreateAccount(accountRepository, userRepository);
  }

  @Bean
  public CreateStaff createStaff(
      StaffRepository staffRepository,
      UserRepository userRepository,
      dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository restaurantRepository,
      PlanLimitValidator planLimitValidator) {
    return new CreateStaff(
        staffRepository, userRepository, restaurantRepository, planLimitValidator);
  }

  @Bean
  public UpdateStaff editStaff(StaffRepository staffRepository) {
    return new UpdateStaff(staffRepository);
  }

  @Bean
  public DeleteStaff deleteStaff(StaffRepository staffRepository) {
    return new DeleteStaff(staffRepository);
  }

  @Bean
  public GetStaff getStaff(StaffRepository staffRepository) {
    return new GetStaff(staffRepository);
  }

  @Bean
  public GetAccount getAccount(AccountRepository accountRepository) {
    return new GetAccount(accountRepository);
  }
}
