package dev.thiagooliveira.tablesplit.infrastructure.config.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.CreateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantConfig {

  @Bean
  public GetRestaurant getRestaurant(RestaurantRepository restaurantRepository) {
    return new GetRestaurant(restaurantRepository);
  }

  @Bean
  public UpdateRestaurant updateRestaurant(
      GetRestaurant getRestaurant, RestaurantRepository restaurantRepository) {
    return new UpdateRestaurant(getRestaurant, restaurantRepository);
  }

  @Bean
  public CreateRestaurant createRestaurant(
      GetRestaurant getRestaurant, RestaurantRepository restaurantRepository) {
    return new CreateRestaurant(getRestaurant, restaurantRepository);
  }
}
