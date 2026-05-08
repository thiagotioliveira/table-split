package dev.thiagooliveira.tablesplit.infrastructure.menu.persistence;

import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemImageEntityMapper {

  ItemImage toDomain(ItemImageEntity entity);

  ItemImageEntity toEntity(ItemImage domain);
}
