package dev.thiagooliveira.tablesplit.infrastructure.security;

import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.application.account.GetStaff;
import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;
  private final GetStaff getStaff;

  @Value("${app.cleaner.username}")
  private String systemUsername;

  @Value("${app.cleaner.password}")
  private String systemPassword;

  public CustomUserDetailsService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository,
      GetStaff getStaff) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
    this.getStaff = getStaff;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (systemUsername != null && !systemUsername.isBlank() && systemUsername.equals(email)) {
      return User.withUsername(systemUsername).password(systemPassword).roles("SYSTEM").build();
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String slug = (attributes != null) ? attributes.getRequest().getParameter("slug") : null;

    if (slug == null || slug.isBlank()) {
      var user =
          this.userRepository
              .findByEmail(email)
              .orElseThrow(() -> new UsernameNotFoundException("user not found"));

      var account =
          this.accountRepository
              .findById(user.getAccountId())
              .orElseThrow(() -> new InternalAuthenticationServiceException("account not found"));
      var restaurant =
          this.restaurantRepository
              .findByAccountId(user.getAccountId())
              .orElseThrow(
                  () -> new InternalAuthenticationServiceException("restaurant not found"));
      return new AccountContext(account, user, restaurant);
    }

    // Step 1: Find restaurant in PUBLIC schema (Context is null/public here)
    var restaurant =
        this.restaurantRepository
            .findBySlug(slug)
            .orElseThrow(() -> new UsernameNotFoundException("restaurant not found"));

    // Step 2: Set TenantContext to reach staff table in tenant schema
    TenantContext.setCurrentTenant(TenantContext.generateTenantIdentifier(restaurant.getId()));

    // Step 3: Find staff in Tenant schema
    var staff =
        this.getStaff
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("staff user not found"));

    var account =
        this.accountRepository
            .findById(restaurant.getAccountId())
            .orElseThrow(() -> new InternalAuthenticationServiceException("account not found"));

    return new AccountContext(account, staff, restaurant);
  }
}
