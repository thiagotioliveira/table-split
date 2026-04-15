package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.Staff;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetStaff {

  private final StaffRepository staffRepository;

  public GetStaff(StaffRepository staffRepository) {
    this.staffRepository = staffRepository;
  }

  public Optional<Staff> execute(UUID id) {
    return this.staffRepository.findById(id);
  }

  public List<Staff> list(UUID restaurantId) {
    return this.staffRepository.findByRestaurantId(restaurantId);
  }

  public Optional<Staff> findByEmail(String email) {
    return this.staffRepository.findByEmail(email);
  }
}
