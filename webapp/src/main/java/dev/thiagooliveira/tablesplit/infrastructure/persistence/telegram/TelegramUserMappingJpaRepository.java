package dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserMappingJpaRepository
    extends JpaRepository<TelegramUserMappingEntity, Long> {
  List<TelegramUserMappingEntity> findByRestaurantId(UUID restaurantId);
}
