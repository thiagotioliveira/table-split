package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingStaffPasswordJpaRepository
    extends JpaRepository<PendingStaffPasswordEntity, UUID> {}
