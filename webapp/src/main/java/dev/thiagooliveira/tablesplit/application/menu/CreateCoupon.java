package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateCouponCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import java.util.UUID;

public class CreateCoupon {

  private final CouponRepository couponRepository;

  public CreateCoupon(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  public Coupon execute(UUID restaurantId, CreateCouponCommand command) {
    var coupon = new Coupon();
    coupon.setId(UUID.randomUUID());
    coupon.setRestaurantId(restaurantId);
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
