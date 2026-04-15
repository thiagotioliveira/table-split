package dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery;

import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import dev.thiagooliveira.tablesplit.application.restaurant.DeleteRestaurantImage;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurantImages;
import dev.thiagooliveira.tablesplit.application.restaurant.SetRestaurantCoverImage;
import dev.thiagooliveira.tablesplit.application.restaurant.UploadRestaurantImage;
import dev.thiagooliveira.tablesplit.application.restaurant.command.UploadRestaurantImageCommand;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery.model.RestaurantImageModel;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gallery")
@ManagerModule(Module.GALLERY)
public class GalleryController {

  private final TransactionalContext transactionalContext;
  private final GetRestaurantImages getRestaurantImages;
  private final UploadRestaurantImage uploadRestaurantImage;
  private final DeleteRestaurantImage deleteRestaurantImage;
  private final SetRestaurantCoverImage setRestaurantCoverImage;

  public GalleryController(
      TransactionalContext transactionalContext,
      GetRestaurantImages getRestaurantImages,
      UploadRestaurantImage uploadRestaurantImage,
      DeleteRestaurantImage deleteRestaurantImage,
      SetRestaurantCoverImage setRestaurantCoverImage) {
    this.transactionalContext = transactionalContext;
    this.getRestaurantImages = getRestaurantImages;
    this.uploadRestaurantImage = uploadRestaurantImage;
    this.deleteRestaurantImage = deleteRestaurantImage;
    this.setRestaurantCoverImage = setRestaurantCoverImage;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = new ContextModel(auth);
    var restaurantId = context.getRestaurant().getId();
    List<RestaurantImageModel> images =
        getRestaurantImages.execute(restaurantId).stream().map(RestaurantImageModel::new).toList();

    model.addAttribute("module", Module.GALLERY);
    model.addAttribute("context", context);
    model.addAttribute("images", images);
    return "gallery";
  }

  @PostMapping
  public String uploadImage(
      Authentication auth,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "cover", defaultValue = "false") boolean cover,
      RedirectAttributes redirectAttributes)
      throws IOException {
    var context = (AccountContext) auth.getPrincipal();
    var restaurantId = context.getRestaurant().getId();
    var accountId = context.getId();

    ImageData imageData =
        new ImageData(file.getOriginalFilename(), file.getContentType(), file.getBytes());

    transactionalContext.execute(
        () -> {
          try {
            uploadRestaurantImage.execute(
                new UploadRestaurantImageCommand(accountId, restaurantId, imageData, false));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.gallery.uploaded"));
    return "redirect:/gallery";
  }

  @PostMapping("/{imageId}/delete")
  public String deleteImage(
      Authentication auth,
      @PathVariable("imageId") UUID imageId,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    var restaurantId = context.getRestaurant().getId();
    var accountId = context.getId();

    transactionalContext.execute(
        () -> deleteRestaurantImage.execute(accountId, restaurantId, imageId));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.gallery.deleted"));
    return "redirect:/gallery";
  }

  @PostMapping("/{imageId}/cover")
  public String setCover(
      Authentication auth,
      @PathVariable("imageId") UUID imageId,
      @RequestParam(value = "isCover", defaultValue = "true") boolean isCover,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    var restaurantId = context.getRestaurant().getId();

    transactionalContext.execute(
        () -> setRestaurantCoverImage.execute(restaurantId, imageId, isCover));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.gallery.cover.set"));
    return "redirect:/gallery";
  }
}
