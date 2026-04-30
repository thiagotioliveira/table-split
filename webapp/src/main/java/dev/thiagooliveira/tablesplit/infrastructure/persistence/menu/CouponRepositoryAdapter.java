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

  public CouponRepositoryAdapter(CouponJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Coupon save(Coupon coupon) {
    return repository.save(CouponEntity.fromDomain(coupon)).toDomain();
  }

  @Override
  public Optional<Coupon> findById(UUID id) {
    return repository.findById(id).map(CouponEntity::toDomain);
  }

  @Override
  public List<Coupon> findByRestaurantId(UUID restaurantId) {
    return repository.findByRestaurantId(restaurantId).stream()
        .map(CouponEntity::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Coupon> findByCodeAndRestaurantId(String code, UUID restaurantId) {
    return repository.findByCodeAndRestaurantId(code, restaurantId).map(CouponEntity::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
