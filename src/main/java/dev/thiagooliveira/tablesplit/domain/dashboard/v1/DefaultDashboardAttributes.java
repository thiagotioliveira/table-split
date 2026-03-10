package dev.thiagooliveira.tablesplit.domain.dashboard.v1;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefaultDashboardAttributes {
  private String userFirstName;
  private String restaurantName;
  private String restaurantAddress;
  private String restaurantSlug;
  private List<RecentItem> recentItems = new ArrayList<>();
  private long totalCategories;
  private long totalItems;
  private long totalItemsActive;
  private long totalItemsInactive;

  public DefaultDashboardAttributes() {}

  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public String getRestaurantAddress() {
    return restaurantAddress;
  }

  public void setRestaurantAddress(String restaurantAddress) {
    this.restaurantAddress = restaurantAddress;
  }

  public String getRestaurantSlug() {
    return restaurantSlug;
  }

  public void setRestaurantSlug(String restaurantSlug) {
    this.restaurantSlug = restaurantSlug;
  }

  public String getUserFirstName() {
    return userFirstName;
  }

  public void setUserFirstName(String userFirstName) {
    this.userFirstName = userFirstName;
  }

  public List<RecentItem> getRecentItems() {
    return recentItems;
  }

  public void setRecentItems(List<RecentItem> recentItems) {
    this.recentItems = recentItems;
  }

  public long getTotalCategories() {
    return totalCategories;
  }

  public void setTotalCategories(long totalCategories) {
    this.totalCategories = totalCategories;
  }

  public long getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(long totalItems) {
    this.totalItems = totalItems;
  }

  public long getTotalItemsActive() {
    return totalItemsActive;
  }

  public void setTotalItemsActive(long totalItemsActive) {
    this.totalItemsActive = totalItemsActive;
  }

  public long getTotalItemsInactive() {
    return totalItemsInactive;
  }

  public void setTotalItemsInactive(long totalItemsInactive) {
    this.totalItemsInactive = totalItemsInactive;
  }

  public static class RecentItem {
    private final UUID id;
    private final UUID categoryId;
    private final Map<Language, String> name;
    private final String imageUrl;
    private final BigDecimal price;

    public RecentItem(
        UUID id, UUID categoryId, Map<Language, String> name, String imageUrl, BigDecimal price) {
      this.id = id;
      this.categoryId = categoryId;
      this.name = name;
      this.imageUrl = imageUrl;
      this.price = price;
    }

    public UUID getId() {
      return id;
    }

    public UUID getCategoryId() {
      return categoryId;
    }

    public Map<Language, String> getName() {
      return name;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public BigDecimal getPrice() {
      return price;
    }
  }
}
