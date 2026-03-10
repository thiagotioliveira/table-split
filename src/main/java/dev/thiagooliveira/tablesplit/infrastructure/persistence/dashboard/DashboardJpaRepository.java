package dev.thiagooliveira.tablesplit.infrastructure.persistence.dashboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardJpaRepository extends JpaRepository<DashboardEntity, UUID> {
  Optional<DashboardEntity> findByUserId(UUID userId);

  List<DashboardEntity> findByAccountId(UUID accountId);
}
