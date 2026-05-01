package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CouponRepositoryAdapter implements CouponRepository {

  private final CouponJpaRepository repository;
  private final CouponEntityMapper mapper;

  public CouponRepositoryAdapter(CouponJpaRepository repository, CouponEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Coupon save(Coupon coupon) {
    return mapper.toDomain(repository.save(mapper.toEntity(coupon)));
  }

  @Override
  public Optional<Coupon> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Coupon> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Coupon> findByCodeAndRestaurantId(String code, UUID restaurantId) {
    return repository.findByCodeAndRestaurantId(code, restaurantId).map(mapper::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
