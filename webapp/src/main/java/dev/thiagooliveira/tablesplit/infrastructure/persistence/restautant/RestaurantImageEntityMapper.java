package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantImageEntityMapper {

  RestaurantImage toDomain(RestaurantImageEntity entity);

  RestaurantImageEntity toEntity(RestaurantImage domain);
}
