package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ComboRepositoryAdapter implements ComboRepository {

  private final ComboJpaRepository repository;
  private final ComboEntityMapper mapper;

  public ComboRepositoryAdapter(ComboJpaRepository repository, ComboEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Combo save(Combo combo) {
    return mapper.toDomain(repository.save(mapper.toEntity(combo)));
  }

  @Override
  public Optional<Combo> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Combo> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
