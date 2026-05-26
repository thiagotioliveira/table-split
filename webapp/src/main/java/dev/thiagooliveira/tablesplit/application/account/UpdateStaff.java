package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;

public class UpdateStaff {

  private final StaffRepository staffRepository;

  public UpdateStaff(StaffRepository staffRepository) {
    this.staffRepository = staffRepository;
  }

  public Staff execute(UpdateStaffCommand command) {
    var staff =
        this.staffRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

    staff.update(
        command.firstName(),
        command.lastName(),
        staff.getEmail(),
        command.phone(),
        command.enabled(),
        command.modules(),
        command.password());

    this.staffRepository.save(staff);

    return staff;
  }
}
