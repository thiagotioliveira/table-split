package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingRegistrationJpaRepository
    extends JpaRepository<PendingRegistrationEntity, UUID> {
  Optional<PendingRegistrationEntity> findByEmail(String email);

  void deleteByEmail(String email);
}
