package dev.thiagooliveira.tablesplit.infrastructure.media.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryImageStorage implements ImageStorage {
  private final Cloudinary cloudinary;
  private final String rootFolder;

  public CloudinaryImageStorage(
      Cloudinary cloudinary, @Value("${cloudinary.root-folder}") String rootFolder) {
    this.cloudinary = cloudinary;
    this.rootFolder = rootFolder;
  }

  @Override
  public String uploadItem(
      ImageData image, UUID accountId, UUID restaurantId, UUID itemId, UUID imageId) {
    try {
      Map uploadResult =
          cloudinary
              .uploader()
              .upload(
                  image.content(),
                  Map.of(
                      "folder",
                      folder(rootFolder, accountId, restaurantId, itemId),
                      "public_id",
                      String.format("%s", imageId),
                      "overwrite",
                      false));

      return uploadResult.get("secure_url").toString();

    } catch (IOException e) {
      throw new InfrastructureException("error.image.item.upload", e);
    }
  }

  @Override
  public Map<String, Object> deleteItem(
      UUID accountId, UUID restaurantId, UUID itemId, UUID imageId) {
    String publicId =
        String.format("%s/%s", folder(rootFolder, accountId, restaurantId, itemId), imageId);
    return deleteItem(publicId);
  }

  @Override
  public String uploadRestaurantGallery(
      ImageData image, UUID accountId, UUID restaurantId, UUID imageId) {
    try {
      Map<String, Object> uploadResult =
          cloudinary
              .uploader()
              .upload(
                  image.content(),
                  Map.of(
                      "folder",
                      folderRestaurant(rootFolder, accountId, restaurantId),
                      "public_id",
                      String.format("%s", imageId),
                      "overwrite",
                      false));

      return uploadResult.get("secure_url").toString();

    } catch (IOException e) {
      throw new InfrastructureException("error.image.restaurant.upload", e);
    }
  }

  @Override
  public Map<String, Object> deleteRestaurantGallery(
      UUID accountId, UUID restaurantId, UUID imageId) {
    String publicId =
        String.format("%s/%s", folderRestaurant(rootFolder, accountId, restaurantId), imageId);
    return deleteItem(publicId);
  }

  private static String folderRestaurant(String rootFolder, UUID accountId, UUID restaurantId) {
    return String.format(
        "%s/accounts/%s/restaurants/%s/gallery", rootFolder, accountId, restaurantId);
  }

  private static String folder(String rootFolder, UUID accountId, UUID restaurantId, UUID itemId) {
    return String.format(
        "%s/accounts/%s/restaurants/%s/items/%s", rootFolder, accountId, restaurantId, itemId);
  }

  private Map<String, Object> deleteItem(String imageId) {
    try {
      return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new InfrastructureException("error.image.item.delete", e);
    }
  }
}
