package dev.thiagooliveira.tablesplit.infrastructure.media.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImageData;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImageStorage;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryItemImageStorage implements ItemImageStorage {

  private final Cloudinary cloudinary;
  private final String rootFolder;

  public CloudinaryItemImageStorage(
      Cloudinary cloudinary, @Value("${cloudinary.root-folder}") String rootFolder) {
    this.cloudinary = cloudinary;
    this.rootFolder = rootFolder;
  }

  @Override
  public String upload(
      ItemImageData image, UUID accountId, UUID restaurantId, UUID itemId, UUID imageId) {
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
  public Map<String, Object> delete(UUID accountId, UUID restaurantId, UUID itemId, UUID imageId) {
    String publicId =
        String.format("%s/%s", folder(rootFolder, accountId, restaurantId, itemId), imageId);
    return deleteItemImageById(publicId);
  }

  private Map<String, Object> deleteItemImageById(String imageId) {
    try {
      return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new InfrastructureException("error.image.item.delete", e);
    }
  }

  private static String folder(String rootFolder, UUID accountId, UUID restaurantId, UUID itemId) {
    return String.format(
        "%s/accounts/%s/restaurants/%s/items/%s", rootFolder, accountId, restaurantId, itemId);
  }
}
