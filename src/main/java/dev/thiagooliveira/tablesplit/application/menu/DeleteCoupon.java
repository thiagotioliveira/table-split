package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class DeleteCoupon {

  private final CouponRepository couponRepository;

  public DeleteCoupon(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  public void execute(UUID id) {
    couponRepository.deleteById(id);
  }
}
