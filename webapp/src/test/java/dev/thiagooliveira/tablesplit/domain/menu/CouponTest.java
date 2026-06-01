package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CouponTest {

  @Test
  void shouldGetAndSetFieldsSuccessfully() {
    Coupon coupon = new Coupon();
    UUID id = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID freeItemId = UUID.randomUUID();
    LocalDate date = LocalDate.now();

    coupon.setId(id);
    coupon.setRestaurantId(restaurantId);
    coupon.setCode("DISCOUNT10");
    coupon.setName("10% off");
    coupon.setDiscountType(DiscountType.PERCENTAGE);
    coupon.setDiscountValue(BigDecimal.TEN);
    coupon.setFreeItemId(freeItemId);
    coupon.setMinOrderValue(BigDecimal.valueOf(50));
    coupon.setValidDate(date);
    coupon.setUsageLimit(100);
    coupon.setUsedCount(10);
    coupon.setActive(true);

    Coupon.CouponRule rule = new Coupon.CouponRule(CouponRuleType.MIN_ITEM_QUANTITY, "3");
    coupon.setRules(List.of(rule));

    assertEquals(id, coupon.getId());
    assertEquals(restaurantId, coupon.getRestaurantId());
    assertEquals("DISCOUNT10", coupon.getCode());
    assertEquals("10% off", coupon.getName());
    assertEquals(DiscountType.PERCENTAGE, coupon.getDiscountType());
    assertEquals(BigDecimal.TEN, coupon.getDiscountValue());
    assertEquals(freeItemId, coupon.getFreeItemId());
    assertEquals(BigDecimal.valueOf(50), coupon.getMinOrderValue());
    assertEquals(date, coupon.getValidDate());
    assertEquals(100, coupon.getUsageLimit());
    assertEquals(10, coupon.getUsedCount());
    assertTrue(coupon.isActive());
    assertEquals(1, coupon.getRules().size());
    assertEquals(CouponRuleType.MIN_ITEM_QUANTITY, coupon.getRules().get(0).type());
    assertEquals("3", coupon.getRules().get(0).value());
  }
}
