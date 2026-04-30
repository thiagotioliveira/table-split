package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCouponCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRepository;
import java.util.UUID;

public class UpdateCoupon {

  private final CouponRepository couponRepository;

  public UpdateCoupon(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  public Coupon execute(UUID restaurantId, UUID couponId, UpdateCouponCommand command) {
    var coupon = couponRepository.findById(couponId).orElseThrow();
    coupon.setCode(command.code().toUpperCase());
    coupon.setName(command.name());
    coupon.setDiscountType(command.discountType());
    coupon.setDiscountValue(command.discountValue());
    coupon.setFreeItemId(command.freeItemId());
    coupon.setMinOrderValue(command.minOrderValue());
    coupon.setValidDate(command.validDate());
    coupon.setUsageLimit(command.usageLimit());
    coupon.setRules(command.rules());
    coupon.setActive(command.active());

    return couponRepository.save(coupon);
  }
}
