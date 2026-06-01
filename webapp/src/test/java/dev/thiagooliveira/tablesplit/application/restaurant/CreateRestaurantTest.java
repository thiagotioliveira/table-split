package dev.thiagooliveira.tablesplit.application.restaurant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateRestaurantTest {

  private RestaurantRepository restaurantRepository;
  private CreateRestaurant createRestaurant;

  @BeforeEach
  void setUp() {
    restaurantRepository = mock(RestaurantRepository.class);
    createRestaurant = new CreateRestaurant(restaurantRepository);
  }

  @Test
  void shouldCreateRestaurantSuccessfully() {
    UUID accountId = UUID.randomUUID();
    CreateRestaurantCommand command =
        new CreateRestaurantCommand(
            "My Gourmet Bistro",
            "gourmet-bistro",
            "A nice bistro",
            "http://bistro.com",
            "+5511999999999",
            "contact@bistro.com",
            "Main St, 123",
            CuisineType.ITALIAN,
            Collections.emptyList(),
            Collections.singletonList(Language.PT),
            Currency.BRL,
            10,
            15,
            AveragePrice.PRICE_20_50,
            Collections.emptyList(),
            "#FFFFFF",
            "#000000",
            Language.PT);

    when(restaurantRepository.findBySlug("gourmet-bistro")).thenReturn(Optional.empty());

    Restaurant result = createRestaurant.execute(accountId, command);

    assertNotNull(result);
    assertEquals("My Gourmet Bistro", result.getName());
    assertEquals("gourmet-bistro", result.getSlug());
    assertEquals(accountId, result.getAccountId());
    assertEquals(Currency.BRL, result.getCurrency());
    assertEquals(10, result.getServiceFee());

    verify(restaurantRepository).save(result);
  }

  @Test
  void shouldThrowSlugAlreadyExistException() {
    UUID accountId = UUID.randomUUID();
    CreateRestaurantCommand command =
        new CreateRestaurantCommand(
            "My Gourmet Bistro",
            "gourmet-bistro",
            "A nice bistro",
            null,
            null,
            null,
            null,
            null,
            null,
            Collections.singletonList(Language.PT),
            Currency.BRL,
            10,
            15,
            AveragePrice.PRICE_20_50,
            Collections.emptyList(),
            "#FFFFFF",
            "#000000",
            Language.PT);

    Restaurant existing = new Restaurant();
    existing.setSlug("gourmet-bistro");

    when(restaurantRepository.findBySlug("gourmet-bistro")).thenReturn(Optional.of(existing));

    assertThrows(SlugAlreadyExist.class, () -> createRestaurant.execute(accountId, command));

    verify(restaurantRepository, never()).save(any());
  }
}
