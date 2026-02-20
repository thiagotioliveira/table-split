package dev.thiagooliveira.tablesplit.infrastructure.config.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantConfig {

  @Bean
  public GetRestaurant getRestaurant(RestaurantRepositoryAdapter restaurantRepositoryAdapter) {
    return new GetRestaurant(restaurantRepositoryAdapter);
  }

  @Bean
  public UpdateRestaurant updateRestaurant(
      GetRestaurant getRestaurant, RestaurantRepositoryAdapter restaurantRepositoryAdapter) {
    return new UpdateRestaurant(getRestaurant, restaurantRepositoryAdapter);
  }
}
