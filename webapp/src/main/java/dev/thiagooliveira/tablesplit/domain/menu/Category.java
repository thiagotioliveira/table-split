package dev.thiagooliveira.tablesplit.domain.menu;

import dev.thiagooliveira.tablesplit.domain.common.AggregateRoot;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.CategoryCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryDeletedEvent;
import dev.thiagooliveira.tablesplit.domain.event.CategoryUpdatedEvent;
import java.util.Map;
import java.util.UUID;

public class Category extends AggregateRoot {
  private UUID id;
  private transient UUID accountId;
  private UUID restaurantId;

  public static Category create(UUID accountId, UUID restaurantId) {
    Category category = new Category();
    category.setId(UUID.randomUUID());
    category.setAccountId(accountId);
    category.setRestaurantId(restaurantId);
    category.registerEvent(new CategoryCreatedEvent(accountId, category.getId()));
    return category;
  }

  public void update() {
    registerEvent(new CategoryUpdatedEvent(this.getAccountId(), this.id));
  }

  public void delete() {
    registerEvent(new CategoryDeletedEvent(this.getAccountId(), this.id));
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  private Integer order;
  private Map<Language, String> name;

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

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Map<Language, String> getName() {
    return name;
  }

  public void setName(Map<Language, String> name) {
    this.name = name;
  }
}
