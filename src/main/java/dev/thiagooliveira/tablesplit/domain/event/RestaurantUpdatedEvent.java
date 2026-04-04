package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import java.util.List;
import java.util.UUID;

public class RestaurantUpdatedEvent implements DomainEvent {
  private final UUID accountId;
  private final UUID restaurantId;
  private final List<Language> addedLanguages;
  private final List<Language> removedLanguages;
  private final Language defaultLanguage;

  public RestaurantUpdatedEvent(
      Restaurant restaurant, List<Language> addedLanguages, List<Language> removedLanguages) {
    this.accountId = restaurant.getAccountId();
    this.restaurantId = restaurant.getId();
    this.addedLanguages = addedLanguages;
    this.removedLanguages = removedLanguages;
    this.defaultLanguage = restaurant.getDefaultLanguage();
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  public List<Language> getAddedLanguages() {
    return addedLanguages;
  }

  public List<Language> getRemovedLanguages() {
    return removedLanguages;
  }

  public Language getDefaultLanguage() {
    return defaultLanguage;
  }
}
