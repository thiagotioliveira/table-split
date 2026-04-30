package dev.thiagooliveira.tablesplit.domain.menu;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.ItemCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.ItemUpdatedEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Item extends AggregateRoot {
  private UUID id;
  private transient UUID accountId;
  private UUID restaurantId;
  private UUID categoryId;
  private List<ItemImage> images = new ArrayList<>();

  public static Item create(UUID accountId, UUID restaurantId) {
    Item item = new Item();
    item.setId(UUID.randomUUID());
    item.setAccountId(accountId);
    item.setRestaurantId(restaurantId);
    item.registerEvent(new ItemCreatedEvent(accountId, item.getId()));
    return item;
  }

  public void update() {
    registerEvent(new ItemUpdatedEvent(this.getAccountId(), this.id));
  }

  public void delete() {
    registerEvent(new ItemDeletedEvent(this.getAccountId(), this.id));
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  private Category category;
  private Map<Language, String> name;
  private Map<Language, String> description;
  private BigDecimal price;
  private PromotionInfo promotion;
  private List<ItemTag> tags;
  private Map<Language, List<ItemQuestion>> questions;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }

  public Map<Language, String> getDescription() {
    return description;
  }

  public void setDescription(Map<Language, String> description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public List<ItemImage> getImages() {
    return images;
  }

  public void setImages(List<ItemImage> images) {
    this.images = images;
  }

  public String getImage() {
    return images.stream()
        .filter(ItemImage::isMain)
        .findFirst()
        .map(ItemImage::getName)
        .orElse(images.isEmpty() ? null : images.get(0).getName());
  }

  public void addImage(UUID imageId, String url, boolean main) {
    ItemImage itemImage = new ItemImage();
    itemImage.setId(imageId);
    itemImage.setItemId(this.id);
    itemImage.setName(url);
    itemImage.setMain(main);
    this.images.add(itemImage);
  }

  public PromotionInfo getPromotion() {
    return promotion;
  }

  public void setPromotion(PromotionInfo promotion) {
    this.promotion = promotion;
  }

  public BigDecimal getEffectivePrice() {
    return promotion != null ? promotion.promotionalPrice() : price;
  }

  public List<ItemTag> getTags() {
    return tags;
  }

  public void setTags(List<ItemTag> tags) {
    this.tags = tags;
  }

  public Map<Language, List<ItemQuestion>> getQuestions() {
    return questions;
  }

  public void setQuestions(Map<Language, List<ItemQuestion>> questions) {
    this.questions = questions;
  }

  public record PromotionInfo(
      UUID promotionId,
      BigDecimal promotionalPrice,
      DiscountType discountType,
      BigDecimal discountValue) {}

  private boolean available = true;

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}
