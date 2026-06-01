package dev.thiagooliveira.tablesplit.application.restaurant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.restaurant.command.RestaurantImageDataCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.command.UploadRestaurantImageCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.ImageSizeExceededException;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageStorage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadRestaurantImageTest {

  private RestaurantRepository restaurantRepository;
  private RestaurantImageStorage restaurantImageStorage;
  private UploadRestaurantImage uploadRestaurantImage;

  private static final long MAX_SIZE = 100L;

  @BeforeEach
  void setUp() {
    restaurantRepository = mock(RestaurantRepository.class);
    restaurantImageStorage = mock(RestaurantImageStorage.class);
    uploadRestaurantImage =
        new UploadRestaurantImage(restaurantRepository, restaurantImageStorage, MAX_SIZE);
  }

  @Test
  void shouldThrowWhenImageExceedsMaxSize() {
    byte[] largeContent = new byte[(int) MAX_SIZE + 1];
    var imageData = new RestaurantImageDataCommand("img.png", "image/png", largeContent);
    var command =
        new UploadRestaurantImageCommand(UUID.randomUUID(), UUID.randomUUID(), imageData, false);

    assertThrows(ImageSizeExceededException.class, () -> uploadRestaurantImage.execute(command));
    verifyNoInteractions(restaurantImageStorage, restaurantRepository);
  }

  @Test
  void shouldUploadImageSuccessfully() throws IOException {
    byte[] content = new byte[10];
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    var imageData = new RestaurantImageDataCommand("photo.jpg", "image/jpeg", content);
    var command = new UploadRestaurantImageCommand(accountId, restaurantId, imageData, true);

    when(restaurantImageStorage.upload(any(), eq(accountId), eq(restaurantId), any()))
        .thenReturn("https://cdn.example.com/photo.jpg");

    RestaurantImage result = uploadRestaurantImage.execute(command);

    assertNotNull(result.getId());
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals("https://cdn.example.com/photo.jpg", result.getName());
    assertTrue(result.isCover());
    verify(restaurantRepository).saveImage(result);
  }

  @Test
  void shouldAcceptImageAtExactMaxSize() {
    byte[] content = new byte[(int) MAX_SIZE];
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    var imageData = new RestaurantImageDataCommand("img.png", "image/png", content);
    var command = new UploadRestaurantImageCommand(accountId, restaurantId, imageData, false);

    when(restaurantImageStorage.upload(any(), eq(accountId), eq(restaurantId), any()))
        .thenReturn("https://cdn.example.com/img.png");

    assertDoesNotThrow(() -> uploadRestaurantImage.execute(command));
  }
}
