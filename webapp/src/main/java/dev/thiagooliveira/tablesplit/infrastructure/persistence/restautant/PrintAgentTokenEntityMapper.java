package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import org.springframework.stereotype.Component;

@Component
public class PrintAgentTokenEntityMapper {

  public PrintAgentToken toDomain(PrintAgentTokenEntity entity) {
    var domain = new PrintAgentToken();
    domain.setId(entity.getId());
    domain.setToken(entity.getToken());
    domain.setRestaurantId(entity.getRestaurant().getId());
    domain.setCreatedAt(entity.getCreatedAt());
    domain.setLastUsedAt(entity.getLastUsedAt());
    return domain;
  }

  public PrintAgentTokenEntity toEntity(PrintAgentToken domain) {
    var entity = new PrintAgentTokenEntity();
    entity.setId(domain.getId());
    entity.setToken(domain.getToken());
    var restaurant = new RestaurantEntity();
    restaurant.setId(domain.getRestaurantId());
    entity.setRestaurant(restaurant);
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setLastUsedAt(domain.getLastUsedAt());
    return entity;
  }
}
