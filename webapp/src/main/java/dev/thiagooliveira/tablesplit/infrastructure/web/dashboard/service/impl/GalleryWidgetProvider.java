package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurantImages;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GalleryImageItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.GalleryWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class GalleryWidgetProvider implements WidgetProvider {

  private final GetRestaurantImages getRestaurantImages;
  private final org.springframework.context.MessageSource messageSource;

  public GalleryWidgetProvider(
      GetRestaurantImages getRestaurantImages,
      org.springframework.context.MessageSource messageSource) {
    this.getRestaurantImages = getRestaurantImages;
    this.messageSource = messageSource;
  }

  @Override
  public Module getRequiredModule() {
    return Module.GALLERY;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();
    List<RestaurantImage> dbImages = getRestaurantImages.execute(restaurantId);

    List<GalleryImageItem> items = new ArrayList<>();
    int totalCount = dbImages.size();

    if (dbImages.isEmpty()) {
      // Curated fallbacks to wow the user if no images are uploaded yet
      String[] fallbacks = {
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=150&h=150&fit=crop",
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=150&h=150&fit=crop",
        "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?w=150&h=150&fit=crop",
        "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=150&h=150&fit=crop",
        "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=150&h=150&fit=crop"
      };
      for (String url : fallbacks) {
        GalleryImageItem item = new GalleryImageItem();
        item.setImageUrl(url);
        items.add(item);
      }
      totalCount = fallbacks.length;
    } else {
      for (int i = 0; i < Math.min(5, dbImages.size()); i++) {
        GalleryImageItem item = new GalleryImageItem();
        item.setImageUrl(dbImages.get(i).getName());
        items.add(item);
      }
    }

    GalleryWidgetContent content = new GalleryWidgetContent();
    content.setImages(items);
    content.setTotalCount(totalCount);
    content.setNewThisWeekCount(totalCount > 3 ? 3 : totalCount);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("gallery");
    widget.setType(DashboardWidgetResponse.TypeEnum.GALLERY);
    widget.setTitle(
        messageSource.getMessage(
            "dashboard.widget.gallery.title", null, "Galeria de Fotos", locale));
    widget.setSize(DashboardWidgetResponse.SizeEnum.MEDIUM);
    widget.setOrder(11);
    widget.setGalleryContent(content);

    return widget;
  }
}
