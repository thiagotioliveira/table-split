package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouponConfig {

  @Bean
  public CreateCoupon createCoupon(CouponRepository couponRepository) {
    return new CreateCoupon(couponRepository);
  }

  @Bean
  public UpdateCoupon updateCoupon(CouponRepository couponRepository) {
    return new UpdateCoupon(couponRepository);
  }

  @Bean
  public DeleteCoupon deleteCoupon(CouponRepository couponRepository) {
    return new DeleteCoupon(couponRepository);
  }

  @Bean
  public GetCoupons getCoupons(CouponRepository couponRepository) {
    return new GetCoupons(couponRepository);
  }

  @Bean
  public ToggleCoupon toggleCoupon(CouponRepository couponRepository) {
    return new ToggleCoupon(couponRepository);
  }
}
