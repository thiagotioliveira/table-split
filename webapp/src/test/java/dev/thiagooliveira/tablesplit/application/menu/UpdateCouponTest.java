package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCouponCommand;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateCouponTest {

  private CouponRepository couponRepository;
  private UpdateCoupon updateCoupon;

  @BeforeEach
  void setUp() {
    couponRepository = mock(CouponRepository.class);
    updateCoupon = new UpdateCoupon(couponRepository);
  }

  @Test
  void shouldUpdateCouponSuccessfully() {
    UUID couponId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    Coupon coupon = new Coupon();
    coupon.setId(couponId);
    coupon.setRestaurantId(restaurantId);

    UpdateCouponCommand command =
        new UpdateCouponCommand(
            "discount50",
            "50% off",
            DiscountType.PERCENTAGE,
            BigDecimal.valueOf(50),
            UUID.randomUUID(),
            BigDecimal.TEN,
            LocalDate.now().plusDays(5),
            200,
            List.of(),
            true);

    when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
    when(couponRepository.save(any(Coupon.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Coupon result = updateCoupon.execute(restaurantId, couponId, command);

    assertNotNull(result);
    assertEquals("DISCOUNT50", result.getCode());
    assertEquals("50% off", result.getName());
    assertEquals(DiscountType.PERCENTAGE, result.getDiscountType());
    assertEquals(BigDecimal.valueOf(50), result.getDiscountValue());
    assertEquals(command.freeItemId(), result.getFreeItemId());
    assertEquals(BigDecimal.TEN, result.getMinOrderValue());
    assertEquals(command.validDate(), result.getValidDate());
    assertEquals(200, result.getUsageLimit());
    assertTrue(result.isActive());

    verify(couponRepository).save(coupon);
  }

  @Test
  void shouldThrowExceptionWhenCouponNotFound() {
    UUID couponId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> updateCoupon.execute(restaurantId, couponId, null));
  }
}
