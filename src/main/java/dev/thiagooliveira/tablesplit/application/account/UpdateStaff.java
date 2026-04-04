package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.StaffAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Staff;

public class UpdateStaff {

  private final StaffRepository staffRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;

  public UpdateStaff(
      StaffRepository staffRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository) {
    this.staffRepository = staffRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
  }

  public Staff execute(UpdateStaffCommand command) {
    var staff =
        this.staffRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

    var restaurant =
        this.restaurantRepository
            .findById(staff.getRestaurantId())
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
        .findById(command.id())
        .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

    this.staffRepository
        .findByEmail(command.email())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(command.id())) {
                throw new StaffAlreadyRegisteredException();
              }
            });

    staff.setFirstName(command.firstName());
    staff.setLastName(command.lastName());
    staff.setEmail(command.email());
    staff.setPhone(command.phone());
    staff.setEnabled(command.enabled());
    staff.setModules(command.modules());

    if (command.password() != null && !command.password().isBlank()) {
      staff.setPassword(command.password());
    }

    this.staffRepository.save(staff);
    return staff;
  }
}
