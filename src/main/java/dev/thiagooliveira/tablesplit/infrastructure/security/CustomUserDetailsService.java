package dev.thiagooliveira.tablesplit.infrastructure.security;

import dev.thiagooliveira.tablesplit.infrastructure.persistence.account.UserJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.RestaurantContext;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserJpaRepository userJpaRepository;
  private final RestaurantJpaRepository restaurantJpaRepository;

  public CustomUserDetailsService(
      UserJpaRepository userJpaRepository, RestaurantJpaRepository restaurantJpaRepository) {
    this.userJpaRepository = userJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
  }

  @Override
  public UserContext loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = this.userJpaRepository.findByEmail(email).orElseThrow(); // TODO
    var restaurant =
        this.restaurantJpaRepository.findByAccountId(user.getAccountId()).orElseThrow(); // TODO
    return new UserContext(
        user.getAccountId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPassword(),
        new RestaurantContext(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getCurrency(),
            restaurant.getCustomerLanguages()));
  }
}
