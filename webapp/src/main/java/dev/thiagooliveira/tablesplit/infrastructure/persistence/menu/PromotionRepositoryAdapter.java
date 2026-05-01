package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PromotionRepositoryAdapter implements PromotionRepository {

  private final PromotionJpaRepository repository;
  private final PromotionEntityMapper mapper;

  public PromotionRepositoryAdapter(
      PromotionJpaRepository repository, PromotionEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Promotion save(Promotion promotion) {
    return mapper.toDomain(repository.save(mapper.toEntity(promotion)));
  }

  @Override
  public Optional<Promotion> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Promotion> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public long count(UUID restaurantId) {
    return repository.countByRestaurantId(restaurantId);
  }
}
