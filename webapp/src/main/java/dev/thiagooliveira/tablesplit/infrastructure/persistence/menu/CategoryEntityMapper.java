package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryEntityMapper {

  @Mapping(target = "name", expression = "java(mapName(entity))")
  @Mapping(source = "numOrder", target = "order")
  Category toDomain(CategoryEntity entity);

  @Mapping(
      target = "name",
      expression =
          "java(dev.thiagooliveira.tablesplit.infrastructure.persistence.common.LocalizedTextEntity.fromMap(domain.getName()))")
  @Mapping(source = "order", target = "numOrder")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "restaurant", ignore = true)
  CategoryEntity toEntity(Category domain);

  default Map<dev.thiagooliveira.tablesplit.domain.common.Language, String> mapName(
      CategoryEntity entity) {
    return entity.getName() != null ? entity.getName().getTranslations() : new HashMap<>();
  }
}
