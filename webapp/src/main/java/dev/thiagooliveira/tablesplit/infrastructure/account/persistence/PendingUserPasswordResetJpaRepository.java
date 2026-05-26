package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingUserPasswordResetJpaRepository
    extends JpaRepository<PendingUserPasswordResetEntity, UUID> {}
