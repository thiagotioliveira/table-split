package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.restaurant.command.UploadRestaurantImageCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import java.io.IOException;
import java.util.UUID;

public class UploadRestaurantImage {
  private final RestaurantRepository restaurantRepository;
  private final ImageStorage imageStorage;
  private final long maxImageSize;

  public UploadRestaurantImage(
      RestaurantRepository restaurantRepository, ImageStorage imageStorage, long maxImageSize) {
    this.restaurantRepository = restaurantRepository;
    this.imageStorage = imageStorage;
    this.maxImageSize = maxImageSize;
  }

  public RestaurantImage execute(UploadRestaurantImageCommand command) throws IOException {
    if (command.imageData() != null
        && command.imageData().content() != null
        && command.imageData().content().length > this.maxImageSize) {
      throw new dev.thiagooliveira.tablesplit.application.exception.ImageSizeExceededException(
          "error.restaurant.image.size");
    }

    var imageId = UUID.randomUUID();
    String url =
        imageStorage.uploadRestaurantGallery(
            command.imageData(), command.accountId(), command.restaurantId(), imageId);

    RestaurantImage image = new RestaurantImage();
    image.setId(imageId);
    image.setRestaurantId(command.restaurantId());
    image.setName(url);
    image.setCover(command.cover());

    restaurantRepository.saveImage(image);
    return image;
  }
}
