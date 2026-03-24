package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.restaurant.command.UploadRestaurantImageCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import java.io.IOException;
import java.util.UUID;

public class UploadRestaurantImage {
  private final RestaurantRepository restaurantRepository;
  private final ImageStorage imageStorage;

  public UploadRestaurantImage(
      RestaurantRepository restaurantRepository, ImageStorage imageStorage) {
    this.restaurantRepository = restaurantRepository;
    this.imageStorage = imageStorage;
  }

  public RestaurantImage execute(UploadRestaurantImageCommand command) throws IOException {
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
