package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetItemTest {

  private ItemRepository itemRepository;
  private PromotionRepository promotionRepository;
  private GetItem getItem;

  @BeforeEach
  void setUp() {
    itemRepository = mock(ItemRepository.class);
    promotionRepository = mock(PromotionRepository.class);
    getItem = new GetItem(itemRepository, promotionRepository);
  }

  @Test
  void shouldListItemsWithoutPromotionsSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    List<Language> languages = List.of(Language.PT);

    Item item1 = new Item();
    item1.setId(UUID.randomUUID());
    item1.setPrice(BigDecimal.valueOf(25.50));

    when(itemRepository.findAll(restaurantId, languages)).thenReturn(List.of(item1));

    List<Item> result = getItem.execute(restaurantId, languages);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNull(result.get(0).getPromotion());
  }

  @Test
  void shouldApplyBestPromotionToItemsSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    List<Language> languages = List.of(Language.PT);

    Item item1 = new Item();
    item1.setId(UUID.randomUUID());
    item1.setRestaurantId(restaurantId);
    item1.setPrice(BigDecimal.valueOf(100.00));

    // Create a 10% percentage discount promotion
    Promotion p1 = new Promotion();
    p1.setId(UUID.randomUUID());
    p1.setRestaurantId(restaurantId);
    p1.setActive(true);
    p1.setApplyType(ApplyType.ALL_MENU);
    p1.setDiscountType(DiscountType.PERCENTAGE);
    p1.setDiscountValue(BigDecimal.valueOf(10)); // 10% off -> price 90.00

    // Create a $15 absolute discount promotion
    Promotion p2 = new Promotion();
    p2.setId(UUID.randomUUID());
    p2.setRestaurantId(restaurantId);
    p2.setActive(true);
    p2.setApplyType(ApplyType.ALL_MENU);
    p2.setDiscountType(DiscountType.FIXED_VALUE);
    p2.setDiscountValue(BigDecimal.valueOf(15)); // $15 off -> price 85.00 (BETTER!)

    when(itemRepository.findAll(restaurantId, languages)).thenReturn(List.of(item1));
    when(promotionRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(p1, p2));

    List<Item> result = getItem.execute(restaurantId, languages, true);

    assertNotNull(result);
    assertEquals(1, result.size());
    Item.PromotionInfo promoInfo = result.get(0).getPromotion();
    assertNotNull(promoInfo);
    assertEquals(p2.getId(), promoInfo.promotionId());
    assertEquals(0, BigDecimal.valueOf(85.00).compareTo(promoInfo.promotionalPrice()));
    assertEquals(DiscountType.FIXED_VALUE, promoInfo.discountType());
    assertEquals(0, BigDecimal.valueOf(15).compareTo(promoInfo.discountValue()));
  }

  @Test
  void shouldFilterOutInactiveOrTimeBoundPromotions() {
    UUID restaurantId = UUID.randomUUID();
    List<Language> languages = List.of(Language.PT);

    Item item1 = new Item();
    item1.setId(UUID.randomUUID());
    item1.setRestaurantId(restaurantId);
    item1.setPrice(BigDecimal.valueOf(50.00));

    // Promotion that is inactive
    Promotion inactive = new Promotion();
    inactive.setId(UUID.randomUUID());
    inactive.setRestaurantId(restaurantId);
    inactive.setActive(false);
    inactive.setApplyType(ApplyType.ALL_MENU);
    inactive.setDiscountType(DiscountType.FIXED_VALUE);
    inactive.setDiscountValue(BigDecimal.valueOf(10));

    // Promotion with future start date
    Promotion future = new Promotion();
    future.setId(UUID.randomUUID());
    future.setRestaurantId(restaurantId);
    future.setActive(true);
    future.setStartDate(LocalDateTime.now().plusDays(2));
    future.setApplyType(ApplyType.ALL_MENU);
    future.setDiscountType(DiscountType.FIXED_VALUE);
    future.setDiscountValue(BigDecimal.valueOf(20));

    when(itemRepository.findAll(restaurantId, languages)).thenReturn(List.of(item1));
    when(promotionRepository.findByRestaurantId(restaurantId))
        .thenReturn(List.of(inactive, future));

    List<Item> result = getItem.execute(restaurantId, languages, true);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNull(result.get(0).getPromotion());
  }

  @Test
  void shouldCountItemStatusStatistics() {
    UUID restaurantId = UUID.randomUUID();

    when(itemRepository.count(restaurantId)).thenReturn(10L);
    when(itemRepository.countActive(restaurantId)).thenReturn(8L);
    when(itemRepository.countInactive(restaurantId)).thenReturn(2L);

    assertEquals(10L, getItem.count(restaurantId));
    assertEquals(8L, getItem.countActive(restaurantId));
    assertEquals(2L, getItem.countInactive(restaurantId));
  }

  @Test
  void shouldFindByIdIncludingDeletedAndApplyPromotion() {
    UUID itemId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();

    Item item = new Item();
    item.setId(itemId);
    item.setRestaurantId(restaurantId);
    item.setPrice(BigDecimal.valueOf(100));

    Promotion p = new Promotion();
    p.setId(UUID.randomUUID());
    p.setRestaurantId(restaurantId);
    p.setActive(true);
    p.setApplyType(ApplyType.ALL_MENU);
    p.setDiscountType(DiscountType.FIXED_VALUE);
    p.setDiscountValue(BigDecimal.TEN);

    when(itemRepository.findByIdIncludingDeleted(itemId)).thenReturn(Optional.of(item));
    when(promotionRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(p));

    Optional<Item> resultOpt = getItem.findByIdIncludingDeleted(itemId, true);
    assertTrue(resultOpt.isPresent());
    assertNotNull(resultOpt.get().getPromotion());
    assertEquals(BigDecimal.valueOf(90), resultOpt.get().getPromotion().promotionalPrice());

    // Without promotions
    item.setPromotion(null);
    Optional<Item> resultOpt2 = getItem.findByIdIncludingDeleted(itemId, false);
    assertTrue(resultOpt2.isPresent());
    assertNull(resultOpt2.get().getPromotion());
  }

  @Test
  void shouldApplyCategoryAndItemSpecificPromotions() {
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Category category = new Category();
    category.setId(categoryId);

    Item item = new Item();
    item.setId(itemId);
    item.setRestaurantId(restaurantId);
    item.setCategory(category);
    item.setPrice(BigDecimal.valueOf(100));

    Promotion catPromo = new Promotion();
    catPromo.setId(UUID.randomUUID());
    catPromo.setRestaurantId(restaurantId);
    catPromo.setActive(true);
    catPromo.setApplyType(ApplyType.CATEGORY);
    catPromo.setApplicableIds(Set.of(categoryId.toString()));
    catPromo.setDiscountType(DiscountType.FIXED_VALUE);
    catPromo.setDiscountValue(BigDecimal.TEN);

    Promotion itemPromo = new Promotion();
    itemPromo.setId(UUID.randomUUID());
    itemPromo.setRestaurantId(restaurantId);
    itemPromo.setActive(true);
    itemPromo.setApplyType(ApplyType.ITEM);
    itemPromo.setApplicableIds(Set.of(itemId.toString()));
    itemPromo.setDiscountType(DiscountType.FIXED_VALUE);
    itemPromo.setDiscountValue(BigDecimal.valueOf(20));

    when(itemRepository.findAll(restaurantId, List.of(Language.PT))).thenReturn(List.of(item));
    when(promotionRepository.findByRestaurantId(restaurantId))
        .thenReturn(List.of(catPromo, itemPromo));

    List<Item> result = getItem.execute(restaurantId, List.of(Language.PT), true);
    assertNotNull(result.get(0).getPromotion());
    // Should choose the itemPromo (20 discount -> price 80) instead of catPromo (10 discount ->
    // price 90)
    assertEquals(BigDecimal.valueOf(80), result.get(0).getPromotion().promotionalPrice());
  }

  @Test
  void shouldFilterPromotionsByDayOfWeekAndTimeOfDay() {
    UUID restaurantId = UUID.randomUUID();
    Item item = new Item();
    item.setId(UUID.randomUUID());
    item.setRestaurantId(restaurantId);
    item.setPrice(BigDecimal.valueOf(100));

    Promotion p = new Promotion();
    p.setId(UUID.randomUUID());
    p.setRestaurantId(restaurantId);
    p.setActive(true);
    p.setApplyType(ApplyType.ALL_MENU);
    p.setDiscountType(DiscountType.FIXED_VALUE);
    p.setDiscountValue(BigDecimal.TEN);

    // Valid on other days, not today
    DayOfWeek otherDay =
        LocalDateTime.now().getDayOfWeek() == DayOfWeek.MONDAY
            ? DayOfWeek.TUESDAY
            : DayOfWeek.MONDAY;
    p.setDaysOfWeek(Set.of(otherDay));

    when(itemRepository.findAll(restaurantId, List.of(Language.PT))).thenReturn(List.of(item));
    when(promotionRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(p));

    List<Item> result = getItem.execute(restaurantId, List.of(Language.PT), true);
    assertNull(result.get(0).getPromotion());

    // Valid today, but only during specific hours (say, early morning 01:00 to 02:00)
    p.setDaysOfWeek(Set.of(LocalDateTime.now().getDayOfWeek()));
    p.setStartTime(LocalTime.of(1, 0));
    p.setEndTime(LocalTime.of(2, 0));

    result = getItem.execute(restaurantId, List.of(Language.PT), true);
    assertNull(result.get(0).getPromotion());
  }
}
