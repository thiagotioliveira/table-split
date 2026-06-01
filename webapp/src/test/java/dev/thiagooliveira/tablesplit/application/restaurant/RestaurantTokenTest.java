package dev.thiagooliveira.tablesplit.application.restaurant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentTokenRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RestaurantTokenTest {

  private PrintAgentTokenRepository tokenRepository;
  private RestaurantRepository restaurantRepository;
  private GetOrCreateToken getOrCreateToken;
  private RegenerateToken regenerateToken;

  @BeforeEach
  void setUp() {
    tokenRepository = mock(PrintAgentTokenRepository.class);
    restaurantRepository = mock(RestaurantRepository.class);
    getOrCreateToken = new GetOrCreateToken(tokenRepository, restaurantRepository);
    regenerateToken = new RegenerateToken(tokenRepository, restaurantRepository);
  }

  @Test
  void shouldGetExistingTokenSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    PrintAgentToken existingToken = new PrintAgentToken(restaurantId, "existing_token_xyz");

    when(tokenRepository.findByRestaurantId(restaurantId)).thenReturn(Optional.of(existingToken));

    String tokenValue = getOrCreateToken.execute(restaurantId);

    assertEquals("existing_token_xyz", tokenValue);
    verify(tokenRepository, never()).save(any());
  }

  @Test
  void shouldCreateNewTokenSuccessfullyWhenNoneExists() {
    UUID restaurantId = UUID.randomUUID();
    when(tokenRepository.findByRestaurantId(restaurantId)).thenReturn(Optional.empty());
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));

    String tokenValue = getOrCreateToken.execute(restaurantId);

    assertNotNull(tokenValue);
    assertFalse(tokenValue.isEmpty());

    ArgumentCaptor<PrintAgentToken> captor = ArgumentCaptor.forClass(PrintAgentToken.class);
    verify(tokenRepository).save(captor.capture());

    PrintAgentToken saved = captor.getValue();
    assertEquals(restaurantId, saved.getRestaurantId());
    assertEquals(tokenValue, saved.getToken());
  }

  @Test
  void shouldThrowExceptionWhenRestaurantDoesNotExistOnCreation() {
    UUID restaurantId = UUID.randomUUID();
    when(tokenRepository.findByRestaurantId(restaurantId)).thenReturn(Optional.empty());
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> getOrCreateToken.execute(restaurantId));

    verify(tokenRepository, never()).save(any());
  }

  @Test
  void shouldRegenerateTokenSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    PrintAgentToken existingToken = new PrintAgentToken(restaurantId, "old_token");

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));
    when(tokenRepository.findByRestaurantId(restaurantId)).thenReturn(Optional.of(existingToken));

    String tokenValue = regenerateToken.execute(restaurantId);

    assertNotNull(tokenValue);
    assertNotEquals("old_token", tokenValue);

    ArgumentCaptor<PrintAgentToken> captor = ArgumentCaptor.forClass(PrintAgentToken.class);
    verify(tokenRepository).save(captor.capture());

    PrintAgentToken saved = captor.getValue();
    assertEquals(restaurantId, saved.getRestaurantId());
    assertEquals(tokenValue, saved.getToken());
  }

  @Test
  void shouldThrowExceptionWhenRestaurantDoesNotExistOnRegeneration() {
    UUID restaurantId = UUID.randomUUID();
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> regenerateToken.execute(restaurantId));

    verify(tokenRepository, never()).save(any());
  }
}
