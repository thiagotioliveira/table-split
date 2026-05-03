package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramIdentityService {
  private static final Logger logger = LoggerFactory.getLogger(TelegramIdentityService.class);

  private final UserRepository userRepository;
  private final StaffRepository staffRepository;
  private final RestaurantRepository restaurantRepository;

  public TelegramIdentityService(
      UserRepository userRepository,
      StaffRepository staffRepository,
      RestaurantRepository restaurantRepository) {
    this.userRepository = userRepository;
    this.staffRepository = staffRepository;
    this.restaurantRepository = restaurantRepository;
  }

  public Optional<IdentifiedUser> identify(String phone) {
    String normalizedPhone = phone.replaceAll("\\s+", "");
    logger.debug("[Identity] Buscando User com telefone normalizado: {}", normalizedPhone);

    // Try exact match first
    TenantContext.setCurrentTenant(null);
    return userRepository
        .findByPhone(normalizedPhone)
        .map(
            user -> {
              logger.info(
                  "[Identity] Usuário encontrado: {} (ID: {}, AccountID: {})",
                  user.getFirstName(),
                  user.getId(),
                  user.getAccountId());
              UUID restaurantId =
                  restaurantRepository
                      .findByAccountId(user.getAccountId())
                      .map(Restaurant::getId)
                      .orElse(null);
              logger.info("[Identity] Restaurante resolvido para a conta: {}", restaurantId);
              return new IdentifiedUser(user.getFirstName(), "Cliente", restaurantId);
            });
  }

  public Optional<IdentifiedUser> identifyStaffBySlug(String slug, String phone) {
    String normalizedPhone = phone.replaceAll("\\s+", "");
    Optional<Restaurant> restaurant = restaurantRepository.findBySlug(slug);
    if (restaurant.isEmpty()) {
      return Optional.empty();
    }

    TenantContext.setCurrentTenant(
        TenantContext.generateTenantIdentifier(restaurant.get().getId()));
    try {
      // Try exact
      Optional<Staff> staff = staffRepository.findByPhone(phone);
      if (staff.isEmpty()) {
        // Try normalized
        staff = staffRepository.findByPhone(normalizedPhone);
      }

      return staff.map(
          s ->
              new IdentifiedUser(
                  s.getFirstName(),
                  "Staff do restaurante " + restaurant.get().getName(),
                  restaurant.get().getId()));
    } finally {
      TenantContext.setCurrentTenant(null);
    }
  }

  public record IdentifiedUser(String name, String role, java.util.UUID restaurantId) {}
}
