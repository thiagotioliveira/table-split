package dev.thiagooliveira.tablesplit.infrastructure.media.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageData;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageStorage;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryRestaurantImageStorage implements RestaurantImageStorage {
  private final Cloudinary cloudinary;
  private final String rootFolder;

  public CloudinaryRestaurantImageStorage(
      Cloudinary cloudinary, @Value("${cloudinary.root-folder}") String rootFolder) {
    this.cloudinary = cloudinary;
    this.rootFolder = rootFolder;
  }

  @Override
  public String upload(RestaurantImageData image, UUID accountId, UUID restaurantId, UUID imageId) {
    try {
      Map<String, Object> uploadResult =
          cloudinary
              .uploader()
              .upload(
                  image.content(),
                  Map.of(
                      "folder",
                      folder(rootFolder, accountId, restaurantId),
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
  public Map<String, Object> delete(UUID accountId, UUID restaurantId, UUID imageId) {
    String publicId = String.format("%s/%s", folder(rootFolder, accountId, restaurantId), imageId);
    return deleteRestaurantImageById(publicId);
  }

  private static String folder(String rootFolder, UUID accountId, UUID restaurantId) {
    return String.format(
        "%s/accounts/%s/restaurants/%s/gallery", rootFolder, accountId, restaurantId);
  }

  private Map<String, Object> deleteRestaurantImageById(String imageId) {
    try {
      return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new InfrastructureException("error.image.item.delete", e);
    }
  }
}
