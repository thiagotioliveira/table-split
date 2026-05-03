package dev.thiagooliveira.tablesplit.domain.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository {
  void save(Staff staff);

  Optional<Staff> findById(UUID id);

  Optional<Staff> findByEmail(String email);

  Optional<Staff> findByPhone(String phone);

  List<Staff> findByRestaurantId(UUID restaurantId);

  void deleteById(UUID id);

  long count(UUID restaurantId);
}
