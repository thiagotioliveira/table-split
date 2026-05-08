package dev.thiagooliveira.tablesplit.infrastructure.restaurant.persistence;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantEntityMapper {

  Restaurant toDomain(RestaurantEntity entity);

  RestaurantEntity toEntity(Restaurant domain);
}
