package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.StaffAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.event.StaffUpdatedEvent;
import java.util.stream.Collectors;

public class UpdateStaff {

  private final StaffRepository staffRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;
  private final EventPublisher eventPublisher;

  public UpdateStaff(
      StaffRepository staffRepository,
      UserRepository userRepository,
      RestaurantRepository restaurantRepository,
      EventPublisher eventPublisher) {
    this.staffRepository = staffRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
    this.eventPublisher = eventPublisher;
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
            (u -> {
              throw new IllegalArgumentException("User already registered");
            }));

    this.staffRepository
        .findByEmail(command.email())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(command.id())) {
                throw new StaffAlreadyRegisteredException();
              }
            });

    var oldModules = staff.getModules();
    var newModules = command.modules();

    var addedModules =
        newModules.stream().filter(m -> !oldModules.contains(m)).collect(Collectors.toSet());

    var removedModules =
        oldModules.stream().filter(m -> !newModules.contains(m)).collect(Collectors.toSet());

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

    eventPublisher.publishEvent(
        new StaffUpdatedEvent(
            staff.getId(),
            restaurant.getId(),
            restaurant.getAccountId(),
            addedModules,
            removedModules));

    return staff;
  }
}
