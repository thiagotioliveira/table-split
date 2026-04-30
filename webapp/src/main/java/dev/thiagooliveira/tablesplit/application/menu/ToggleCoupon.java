package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.CouponRepository;
import java.util.UUID;

public class ToggleCoupon {

  private final CouponRepository couponRepository;

  public ToggleCoupon(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  public void execute(UUID id) {
    couponRepository
        .findById(id)
        .ifPresent(
            coupon -> {
              coupon.setActive(!coupon.isActive());
              couponRepository.save(coupon);
            });
  }
}
