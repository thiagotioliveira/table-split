package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ItemEntityMapper {

  public Item toDomain(ItemEntity entity) {
    var domain = new Item();
    domain.setId(entity.getId());
    domain.setRestaurantId(entity.getCategory().getRestaurantId());
    domain.setCategory(new Category());
    domain.getCategory().setId(entity.getCategory().getId());
    domain
        .getCategory()
        .setName(
            entity.getCategory().getName() != null
                ? entity.getCategory().getName().getTranslations()
                : new HashMap<>());
    domain.getCategory().setRestaurantId(entity.getCategory().getRestaurantId());
    domain.getCategory().setOrder(entity.getCategory().getNumOrder());
    domain.setName(entity.getName() != null ? entity.getName().getTranslations() : new HashMap<>());
    domain.setDescription(
        entity.getDescription() != null
            ? entity.getDescription().getTranslations()
            : new HashMap<>());
    domain.setPrice(entity.getPrice());
    domain.setAvailable(entity.isActive());
    domain.setImages(
        new ArrayList<>(entity.getImages().stream().map(this::imageEntityToDomain).toList()));
    domain.setTags(new ArrayList<>(entity.getTags()));

    Map<Language, List<ItemQuestion>> questionsMap = new HashMap<>();
    if (entity.getQuestions() != null) {
      entity
          .getQuestions()
          .forEach(
              qe ->
                  questionsMap
                      .computeIfAbsent(qe.getLanguage(), k -> new ArrayList<>())
                      .add(qe.toDomain()));
    }
    domain.setQuestions(questionsMap);

    return domain;
  }

  private dev.thiagooliveira.tablesplit.domain.menu.ItemImage imageEntityToDomain(
      ItemImageEntity entity) {
    var domain = new dev.thiagooliveira.tablesplit.domain.menu.ItemImage();
    domain.setId(entity.getId());
    domain.setName(entity.getName());
    domain.setItemId(entity.getItemId());
    domain.setMain(entity.isMain());
    return domain;
  }

  public ItemEntity toEntity(Item domain) {
    var entity = new ItemEntity();
    entity.setId(domain.getId());
    if (domain.getCategory() != null) {
      entity.setCategory(new CategoryEntity());
      entity.getCategory().setId(domain.getCategory().getId());
    } else if (domain.getCategoryId() != null) {
      entity.setCategory(new CategoryEntity());
      entity.getCategory().setId(domain.getCategoryId());
    }
    entity.setName(LocalizedTextEntity.fromMap(domain.getName()));
    entity.setDescription(LocalizedTextEntity.fromMap(domain.getDescription()));
    entity.setPrice(domain.getPrice());
    entity.setActive(domain.isAvailable());
    if (domain.getImages() != null) {
      entity.setImages(
          new HashSet<>(
              domain.getImages().stream()
                  .map(this::imageDomainToEntity)
                  .collect(Collectors.toSet())));
    }
    if (domain.getTags() != null) {
      entity.setTags(new HashSet<>(domain.getTags()));
    }
    if (domain.getQuestions() != null) {
      domain
          .getQuestions()
          .forEach(
              (lang, list) -> {
                if (list != null) {
                  list.forEach(
                      q ->
                          entity
                              .getQuestions()
                              .add(ItemQuestionEntity.fromDomain(q, entity, lang)));
                }
              });
    }
    return entity;
  }

  private ItemImageEntity imageDomainToEntity(
      dev.thiagooliveira.tablesplit.domain.menu.ItemImage domain) {
    var entity = new ItemImageEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setMain(domain.isMain());
    entity.setItemId(domain.getItemId());
    return entity;
  }
}
