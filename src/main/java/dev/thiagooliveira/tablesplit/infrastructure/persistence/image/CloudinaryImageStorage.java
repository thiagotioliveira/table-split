package dev.thiagooliveira.tablesplit.infrastructure.persistence.image;

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
  public String uploadItem(ImageData image, UUID restaurantId, UUID itemId, UUID imageId) {
    try {
      Map uploadResult =
          cloudinary
              .uploader()
              .upload(
                  image.content(),
                  Map.of(
                      "folder",
                      String.format("%s/restaurants/%s/items/%s", rootFolder, restaurantId, itemId),
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
  public Map deleteItem(UUID restaurantId, UUID itemId, UUID imageId) {
    String publicId =
        String.format("%s/restaurants/%s/items/%s/%s", rootFolder, restaurantId, itemId, imageId);
    return deleteItem(publicId);
  }

  @Override
  public Map deleteItem(String imageId) {
    try {
      return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new InfrastructureException("error.image.item.upload", e);
    }
  }
}
