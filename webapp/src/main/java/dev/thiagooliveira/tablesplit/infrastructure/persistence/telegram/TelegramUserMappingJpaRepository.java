package dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TelegramUserMappingJpaRepository
    extends JpaRepository<TelegramUserMappingEntity, Long> {
  List<TelegramUserMappingEntity> findByRestaurantId(UUID restaurantId);

  @Modifying
  @Transactional
  void deleteByChatId(Long chatId);
}
