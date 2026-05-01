package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComboEntityMapper {

  ComboEntityMapper INSTANCE = Mappers.getMapper(ComboEntityMapper.class);

  Combo toDomain(ComboEntity entity);

  ComboEntity toEntity(Combo domain);

  default Combo.ComboItem map(ComboItemEntity entity) {
    if (entity == null) return null;
    return new Combo.ComboItem(entity.getItemId(), entity.getQuantity());
  }

  @Mapping(target = "comboId", ignore = true)
  default ComboItemEntity map(Combo.ComboItem domain) {
    if (domain == null) return null;
    ComboItemEntity entity = new ComboItemEntity();
    if (domain.getItemId() != null) {
      entity.setItemId(UUID.fromString(domain.getItemId()));
    }
    entity.setQuantity(domain.getQuantity());
    return entity;
  }

  @AfterMapping
  default void linkComboItems(@MappingTarget ComboEntity entity) {
    if (entity.getItems() != null && entity.getId() != null) {
      entity.getItems().forEach(item -> item.setComboId(entity.getId()));
    }
  }
}
