package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.command.UploadRestaurantImageCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.ImageSizeExceededException;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageStorage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.io.IOException;
import java.util.UUID;

public class UploadRestaurantImage {
  private final RestaurantRepository restaurantRepository;
  private final RestaurantImageStorage restaurantImageStorage;
  private final long maxImageSize;

  public UploadRestaurantImage(
      RestaurantRepository restaurantRepository,
      RestaurantImageStorage restaurantImageStorage,
      long maxImageSize) {
    this.restaurantRepository = restaurantRepository;
    this.restaurantImageStorage = restaurantImageStorage;
    this.maxImageSize = maxImageSize;
  }

  public RestaurantImage execute(UploadRestaurantImageCommand command) throws IOException {
    if (command.itemImageData() != null
        && command.itemImageData().content() != null
        && command.itemImageData().content().length > this.maxImageSize) {
      throw new ImageSizeExceededException("error.restaurant.image.size");
    }

    var imageId = UUID.randomUUID();
    String url =
        restaurantImageStorage.upload(
            command.itemImageData().toDomain(),
            command.accountId(),
            command.restaurantId(),
            imageId);

    RestaurantImage image = new RestaurantImage();
    image.setId(imageId);
    image.setRestaurantId(command.restaurantId());
    image.setName(url);
    image.setCover(command.cover());

    restaurantRepository.saveImage(image);
    return image;
  }
}
