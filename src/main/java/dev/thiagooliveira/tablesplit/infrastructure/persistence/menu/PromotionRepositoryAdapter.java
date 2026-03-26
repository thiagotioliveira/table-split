package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.PromotionRepository;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PromotionRepositoryAdapter implements PromotionRepository {

  private final PromotionJpaRepository repository;

  public PromotionRepositoryAdapter(PromotionJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Promotion save(Promotion promotion) {
    return repository.save(PromotionEntity.fromDomain(promotion)).toDomain();
  }

  @Override
  public Optional<Promotion> findById(UUID id) {
    return repository.findById(id).map(PromotionEntity::toDomain);
  }

  @Override
  public List<Promotion> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(PromotionEntity::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
