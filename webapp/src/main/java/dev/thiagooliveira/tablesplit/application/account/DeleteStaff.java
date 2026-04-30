package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import java.util.UUID;

public class DeleteStaff {

  private final StaffRepository staffRepository;

  public DeleteStaff(StaffRepository staffRepository) {
    this.staffRepository = staffRepository;
  }

  public void execute(UUID id) {
    this.staffRepository.deleteById(id);
  }
}
