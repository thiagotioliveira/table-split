package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetCoupons {

  private final CouponRepository couponRepository;

  public GetCoupons(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  public List<Coupon> listByRestaurantId(UUID restaurantId) {
    return couponRepository.findByRestaurantId(restaurantId);
  }

  public Optional<Coupon> findById(UUID id) {
    return couponRepository.findById(id);
  }

  public Optional<Coupon> findByCodeAndRestaurantId(String code, UUID restaurantId) {
    return couponRepository.findByCodeAndRestaurantId(code, restaurantId);
  }
}
