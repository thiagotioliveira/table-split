package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.application.account.exception.StaffAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;

public class UpdateStaff {

  private final StaffRepository staffRepository;
  private final UserRepository userRepository;

  public UpdateStaff(StaffRepository staffRepository, UserRepository userRepository) {
    this.staffRepository = staffRepository;
    this.userRepository = userRepository;
  }

  public Staff execute(UpdateStaffCommand command) {
    var staff =
        this.staffRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

    this.userRepository
        .findByEmail(command.email())
        .ifPresent(
            (u -> {
              if (!u.getId().equals(staff.getId())) {
                throw new IllegalArgumentException("User already registered");
              }
            }));

    this.staffRepository
        .findByEmail(command.email())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(command.id())) {
                throw new StaffAlreadyRegisteredException();
              }
            });

    staff.update(
        command.firstName(),
        command.lastName(),
        command.email(),
        command.phone(),
        command.enabled(),
        command.modules(),
        command.password());

    this.staffRepository.save(staff);

    return staff;
  }
}
