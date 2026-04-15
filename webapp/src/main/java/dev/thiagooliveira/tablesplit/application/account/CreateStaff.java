package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.CreateStaffCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.StaffAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import java.util.UUID;

public class CreateStaff {

  private final StaffRepository staffRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;

  public CreateStaff(
      StaffRepository staffRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository) {
    this.staffRepository = staffRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
  }

  public Staff execute(CreateStaffCommand command) {
    var restaurant =
        this.restaurantRepository
            .findById(command.restaurantId())
            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

    this.userRepository
        .findByEmail(command.email())
        .ifPresent(
            owner -> {
              if (owner.getAccountId().equals(restaurant.getAccountId())) {
                throw new StaffAlreadyRegisteredException();
              }
            });

    this.staffRepository
        .findByEmail(command.email())
        .ifPresent(
            existing -> {
              throw new StaffAlreadyRegisteredException();
            });

    var staff = new Staff();
    staff.setId(UUID.randomUUID());
    staff.setRestaurantId(command.restaurantId());
    staff.setFirstName(command.firstName());
    staff.setLastName(command.lastName());
    staff.setEmail(command.email());
    staff.setPhone(command.phone());
    staff.setPassword(command.password()); // Password should be hashed before calling this
    staff.setLanguage(command.language());
    staff.setRole(Role.RESTAURANT_STAFF);
    staff.setEnabled(true);
    staff.setModules(command.modules());

    this.staffRepository.save(staff);
    return staff;
  }
}
