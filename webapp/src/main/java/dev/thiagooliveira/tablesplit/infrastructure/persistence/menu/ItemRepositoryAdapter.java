package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ItemRepositoryAdapter implements ItemRepository {

  private final ItemJpaRepository itemJpaRepository;
  private final ItemQuestionJpaRepository itemQuestionJpaRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;
  private final ItemEntityMapper mapper;

  public ItemRepositoryAdapter(
      ItemJpaRepository itemJpaRepository,
      ItemQuestionJpaRepository itemQuestionJpaRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher,
      ItemEntityMapper mapper) {
    this.itemJpaRepository = itemJpaRepository;
    this.itemQuestionJpaRepository = itemQuestionJpaRepository;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
  }

  @Override
  public Optional<Item> findById(UUID id) {
    return this.itemJpaRepository.findByIdAndDeletedAtIsNull(id).map(this::toDomainWithAccount);
  }

  @Override
  public Optional<Item> findByIdIncludingDeleted(UUID id) {
    return this.itemJpaRepository.findById(id).map(this::toDomainWithAccount);
  }

  private Item toDomainWithAccount(ItemEntity entity) {
    Item item = mapper.toDomain(entity);
    UUID cachedAccountId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
            item.getRestaurantId());
    if (cachedAccountId != null) {
      item.setAccountId(cachedAccountId);
    } else if (entity.getCategory() != null && entity.getCategory().getRestaurant() != null) {
      item.setAccountId(entity.getCategory().getRestaurant().getAccountId());
    }
    return item;
  }

  @Override
  public List<Item> findByRestaurantId(UUID restaurantId) {
    return this.itemJpaRepository
        .findByCategoryRestaurantIdAndDeletedAtIsNull(restaurantId)
        .stream()
        .map(this::toDomainWithAccount)
        .toList();
  }

  @Override
  public List<Item> findAll(UUID restaurantId, List<Language> languages) {
    var projections =
        this.itemJpaRepository.findAllByCategoryRestaurantIdAndLanguages(restaurantId, languages);
    java.util.Map<UUID, Item> itemMap = new java.util.LinkedHashMap<>();
    for (var projection : projections) {
      var domain =
          itemMap.computeIfAbsent(
              projection.item().getId(),
              id -> {
                var i = mapper.toDomain(projection.item());
                i.setName(new java.util.HashMap<>());
                i.setDescription(new java.util.HashMap<>());
                i.setQuestions(new java.util.HashMap<>());
                return i;
              });
      if (projection.nameTranslation() != null) {
        domain.getName().put(projection.language(), projection.nameTranslation());
      }
      if (projection.descriptionTranslation() != null) {
        domain.getDescription().put(projection.language(), projection.descriptionTranslation());
      }
    }

    if (!itemMap.isEmpty()) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              restaurantId);

      itemMap
          .values()
          .forEach(
              i -> {
                if (cachedAccountId != null) {
                  i.setAccountId(cachedAccountId);
                }
              });

      var questions =
          this.itemQuestionJpaRepository.findByItemIdInAndLanguageIn(itemMap.keySet(), languages);
      questions.forEach(
          q -> {
            var item = itemMap.get(q.getItem().getId());
            if (item != null) {
              item.getQuestions()
                  .computeIfAbsent(q.getLanguage(), k -> new java.util.ArrayList<>())
                  .add(q.toDomain());
            }
          });
    }

    return new java.util.ArrayList<>(itemMap.values());
  }

  @Override
  public Item save(Item item) {
    Item savedItem = mapper.toDomain(this.itemJpaRepository.save(mapper.toEntity(item)));

    // Ensure accountId is populated for events
    if (item.getAccountId() == null) {
      UUID cachedAccountId =
          dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.getAccountId(
              item.getRestaurantId());
      item.setAccountId(cachedAccountId);
    }

    item.getDomainEvents().forEach(eventPublisher::publishEvent);
    item.clearEvents();
    return savedItem;
  }

  @Override
  public void delete(UUID itemId) {
    var item = findByIdIncludingDeleted(itemId).orElseThrow();
    item.delete();

    if (this.itemJpaRepository.existsInTicketItems(itemId)) {
      // Exclusão lógica
      var itemEntity = this.itemJpaRepository.findById(itemId).orElseThrow();
      itemEntity.setDeletedAt(dev.thiagooliveira.tablesplit.domain.common.Time.nowOffset());
      itemEntity.setActive(false);
      this.itemJpaRepository.save(itemEntity);
    } else {
      // Exclusão física
      this.itemJpaRepository.deleteById(itemId);
    }

    item.getDomainEvents().forEach(eventPublisher::publishEvent);
    item.clearEvents();
  }

  @Override
  public long count(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantId(restaurantId);
  }

  @Override
  public long countActive(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantIdAndActiveTrue(restaurantId);
  }

  @Override
  public long countInactive(UUID restaurantId) {
    return this.itemJpaRepository.countByCategoryRestaurantIdAndActiveFalse(restaurantId);
  }

  @Override
  public boolean existsInTicketItems(UUID itemId) {
    return this.itemJpaRepository.existsInTicketItems(itemId);
  }
}
