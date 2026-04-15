package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {
  Coupon save(Coupon coupon);

  Optional<Coupon> findById(UUID id);

  List<Coupon> findByRestaurantId(UUID restaurantId);

  Optional<Coupon> findByCodeAndRestaurantId(String code, UUID restaurantId);

  void deleteById(UUID id);
}
