package dev.thiagooliveira.tablesplit.infrastructure.menu.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageJpaRepository extends JpaRepository<ItemImageEntity, UUID> {}
