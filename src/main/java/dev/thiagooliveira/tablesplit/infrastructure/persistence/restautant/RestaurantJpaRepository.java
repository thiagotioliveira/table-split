package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, UUID> {}
