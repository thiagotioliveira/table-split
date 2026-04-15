package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketItemJpaRepository extends JpaRepository<TicketItemEntity, UUID> {

  @Modifying
  @Query("UPDATE TicketItemEntity t SET t.rating = :rating WHERE t.id = :itemId")
  void updateRating(UUID itemId, Integer rating);
}
