package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.application.menu.ComboRepository;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ComboRepositoryAdapter implements ComboRepository {

  private final ComboJpaRepository repository;

  public ComboRepositoryAdapter(ComboJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Combo save(Combo combo) {
    return repository.save(ComboEntity.fromDomain(combo)).toDomain();
  }

  @Override
  public Optional<Combo> findById(UUID id) {
    return repository.findById(id).map(ComboEntity::toDomain);
  }

  @Override
  public List<Combo> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(ComboEntity::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
