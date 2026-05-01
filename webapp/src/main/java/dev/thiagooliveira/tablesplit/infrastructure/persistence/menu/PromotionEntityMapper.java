package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionEntityMapper {

  PromotionEntityMapper INSTANCE = Mappers.getMapper(PromotionEntityMapper.class);

  @Mapping(source = "recurrenceDays", target = "daysOfWeek")
  Promotion toDomain(PromotionEntity entity);

  @Mapping(source = "daysOfWeek", target = "recurrenceDays")
  PromotionEntity toEntity(Promotion domain);
}
