package dev.thiagooliveira.tablesplit.infrastructure.security;

import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;

  public CustomUserDetailsService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
  }

  @Override
  public AccountContext loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = this.userRepository.findByEmail(email).orElseThrow(); // TODO
    var account = this.accountRepository.findById(user.getAccountId()).orElseThrow(); // TODO
    var restaurant =
        this.restaurantRepository.findByAccountId(user.getAccountId()).orElseThrow(); // TODO
    return new AccountContext(account, user, restaurant);
  }
}
