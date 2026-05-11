package dev.thiagooliveira.tablesplit.infrastructure.menu.api;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.CategoryResponse;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.ItemOptionResponse;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.ItemPromotionResponse;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.ItemQuestionResponse;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.ItemResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class MenuApiMapper {

  @Mapping(target = "name", source = "name", qualifiedByName = "mapLocalizedName")
  public abstract CategoryResponse toCategoryResponse(
      Category category, @Context Language language);

  @Mapping(target = "name", source = "name", qualifiedByName = "mapLocalizedName")
  @Mapping(
      target = "description",
      source = "description",
      qualifiedByName = "mapLocalizedDescription")
  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "image", source = "image")
  @Mapping(target = "promotion", source = "promotion")
  @Mapping(target = "questions", source = "questions", qualifiedByName = "mapLocalizedQuestions")
  public abstract ItemResponse toItemResponse(Item item, @Context Language language);

  public abstract ItemPromotionResponse toItemPromotionResponse(Item.PromotionInfo domain);

  @Named("mapLocalizedName")
  protected String mapLocalizedName(Map<Language, String> name, @Context Language language) {
    if (name == null) return null;
    return name.getOrDefault(language, name.values().stream().findFirst().orElse(null));
  }

  @Named("mapLocalizedDescription")
  protected String mapLocalizedDescription(
      Map<Language, String> description, @Context Language language) {
    if (description == null) return null;
    return description.getOrDefault(
        language, description.values().stream().findFirst().orElse(null));
  }

  @Named("mapLocalizedQuestions")
  protected List<ItemQuestionResponse> mapLocalizedQuestions(
      Map<Language, List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>> questions,
      @Context Language language) {
    if (questions == null) return Collections.emptyList();
    List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion> list = questions.get(language);
    if (list == null) return Collections.emptyList();
    return list.stream().map(this::toItemQuestionResponse).collect(Collectors.toList());
  }

  public abstract ItemQuestionResponse toItemQuestionResponse(
      dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion domain);

  public abstract ItemOptionResponse toItemOptionResponse(
      dev.thiagooliveira.tablesplit.domain.menu.ItemOption domain);
}
