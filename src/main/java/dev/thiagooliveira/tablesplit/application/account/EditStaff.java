package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Staff;

public class EditStaff {

  private final StaffRepository staffRepository;

  public EditStaff(StaffRepository staffRepository) {
    this.staffRepository = staffRepository;
  }

  public Staff execute(UpdateStaffCommand command) {
    var staff =
        this.staffRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

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
