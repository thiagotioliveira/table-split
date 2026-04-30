package dev.thiagooliveira.tablesplit.application.restaurant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.restaurant.command.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantUpdatedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateRestaurantTest {

  private RestaurantRepository restaurantRepository;
  private UpdateRestaurant updateRestaurant;

  @BeforeEach
  void setUp() {
    restaurantRepository = mock(RestaurantRepository.class);
    updateRestaurant = new UpdateRestaurant(restaurantRepository);
  }

  @Test
  void shouldUpdateRestaurantMetadataSuccessfully_whenSlugRemainsSame() {
    UUID restaurantId = UUID.randomUUID();
    Restaurant existingRestaurant = new Restaurant();
    existingRestaurant.setId(restaurantId);
    existingRestaurant.setAccountId(UUID.randomUUID());
    existingRestaurant.setSlug("original-slug");
    existingRestaurant.setName("Original Name");

    UpdateRestaurantCommand command = createCommand("original-slug");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

    Restaurant result = updateRestaurant.execute(restaurantId, command);

    assertEquals("Updated Name", result.getName());
    assertEquals("original-slug", result.getSlug());
    verify(restaurantRepository).save(existingRestaurant);
    assertFalse(result.getDomainEvents().isEmpty());
    assertTrue(
        result.getDomainEvents().stream().anyMatch(e -> e instanceof RestaurantUpdatedEvent));
    verify(restaurantRepository, never()).findBySlug(anyString());
  }

  @Test
  void shouldUpdateRestaurantMetadataSuccessfully_whenSlugChangesAndIsAvailable() {
    UUID restaurantId = UUID.randomUUID();
    Restaurant existingRestaurant = new Restaurant();
    existingRestaurant.setId(restaurantId);
    existingRestaurant.setAccountId(UUID.randomUUID());
    existingRestaurant.setSlug("original-slug");

    UpdateRestaurantCommand command = createCommand("new-slug");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
    when(restaurantRepository.findBySlug("new-slug")).thenReturn(Optional.empty());

    Restaurant result = updateRestaurant.execute(restaurantId, command);

    assertEquals("new-slug", result.getSlug());
    verify(restaurantRepository).save(existingRestaurant);
    assertFalse(result.getDomainEvents().isEmpty());
    assertTrue(
        result.getDomainEvents().stream().anyMatch(e -> e instanceof RestaurantUpdatedEvent));
  }

  @Test
  void shouldThrowSlugAlreadyExist_whenNewSlugIsTakenByAnotherRestaurant() {
    UUID restaurantId = UUID.randomUUID();
    Restaurant existingRestaurant = new Restaurant();
    existingRestaurant.setId(restaurantId);
    existingRestaurant.setAccountId(UUID.randomUUID());
    existingRestaurant.setSlug("original-slug");

    UpdateRestaurantCommand command = createCommand("taken-slug");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
    when(restaurantRepository.findBySlug("taken-slug")).thenReturn(Optional.of(new Restaurant()));

    assertThrows(SlugAlreadyExist.class, () -> updateRestaurant.execute(restaurantId, command));

    verify(restaurantRepository, never()).save(any());
  }

  @Test
  void shouldThrowNoSuchElementException_whenRestaurantIsNotFound() {
    UUID restaurantId = UUID.randomUUID();
    UpdateRestaurantCommand command = createCommand("any-slug");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class, () -> updateRestaurant.execute(restaurantId, command));
  }

  @Test
  void shouldRegisterCorrectEventOnSuccess() {
    UUID restaurantId = UUID.randomUUID();
    Restaurant existingRestaurant = new Restaurant();
    existingRestaurant.setId(restaurantId);
    existingRestaurant.setAccountId(UUID.randomUUID());
    existingRestaurant.setSlug("test-slug");

    UpdateRestaurantCommand command = createCommand("test-slug");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

    Restaurant result = updateRestaurant.execute(restaurantId, command);

    var event =
        result.getDomainEvents().stream()
            .filter(e -> e instanceof RestaurantUpdatedEvent)
            .map(e -> (RestaurantUpdatedEvent) e)
            .findFirst()
            .orElseThrow();

    assertEquals(restaurantId, event.getRestaurantId());
  }

  private UpdateRestaurantCommand createCommand(String slug) {
    return new UpdateRestaurantCommand(
        "Updated Name",
        slug,
        "Updated Description",
        "https://updated.com",
        "123456789",
        "updated@test.com",
        "Updated Address",
        null,
        new ArrayList<>(),
        List.of(Language.EN),
        Currency.EUR,
        15,
        AveragePrice.PRICE_20_50,
        new ArrayList<>(),
        "#FFFFFF",
        "#000000");
  }
}
