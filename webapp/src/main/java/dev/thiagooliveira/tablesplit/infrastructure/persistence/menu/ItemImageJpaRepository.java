package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageJpaRepository extends JpaRepository<ItemImageEntity, UUID> {}
