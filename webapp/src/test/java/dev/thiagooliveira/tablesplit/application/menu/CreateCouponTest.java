package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateCouponCommand;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateCouponTest {

  private CouponRepository couponRepository;
  private CreateCoupon createCoupon;

  @BeforeEach
  void setUp() {
    couponRepository = mock(CouponRepository.class);
    createCoupon = new CreateCoupon(couponRepository);
  }

  @Test
  void shouldCreateCouponSuccessfully() {
    UUID restaurantId = UUID.randomUUID();

    CreateCouponCommand command =
        new CreateCouponCommand(
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

    when(couponRepository.save(any(Coupon.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Coupon result = createCoupon.execute(restaurantId, command);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals("DISCOUNT50", result.getCode());
    assertEquals("50% off", result.getName());
    assertEquals(DiscountType.PERCENTAGE, result.getDiscountType());
    assertEquals(BigDecimal.valueOf(50), result.getDiscountValue());
    assertEquals(command.freeItemId(), result.getFreeItemId());
    assertEquals(BigDecimal.TEN, result.getMinOrderValue());
    assertEquals(command.validDate(), result.getValidDate());
    assertEquals(200, result.getUsageLimit());
    assertTrue(result.isActive());

    verify(couponRepository).save(any(Coupon.class));
  }
}
