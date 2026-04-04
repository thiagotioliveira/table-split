package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.application.account.command.CreateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import java.util.UUID;

public class CreateStaff {

  private final StaffRepository staffRepository;

  public CreateStaff(StaffRepository staffRepository) {
    this.staffRepository = staffRepository;
  }

  public Staff execute(CreateStaffCommand command) {
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
