package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintAgentTokenJpaRepository extends JpaRepository<PrintAgentTokenEntity, UUID> {
  Optional<PrintAgentTokenEntity> findByToken(String token);

  Optional<PrintAgentTokenEntity> findByRestaurantId(UUID restaurantId);
}
