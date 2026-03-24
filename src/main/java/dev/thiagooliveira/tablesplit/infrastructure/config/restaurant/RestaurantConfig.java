package dev.thiagooliveira.tablesplit.infrastructure.config.restaurant;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.restaurant.CreateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurantImages;
import dev.thiagooliveira.tablesplit.application.restaurant.UploadRestaurantImage;
import dev.thiagooliveira.tablesplit.application.restaurant.DeleteRestaurantImage;
import dev.thiagooliveira.tablesplit.application.restaurant.SetRestaurantCoverImage;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
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
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    return new UpdateRestaurant(eventPublisher, restaurantRepository);
  }

  @Bean
  public CreateRestaurant createRestaurant(
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    return new CreateRestaurant(eventPublisher, restaurantRepository);
  }

  @Bean
  public GetRestaurantImages getRestaurantImages(RestaurantRepository restaurantRepository) {
    return new GetRestaurantImages(restaurantRepository);
  }

  @Bean
  public UploadRestaurantImage uploadRestaurantImage(
      RestaurantRepository restaurantRepository, ImageStorage imageStorage) {
    return new UploadRestaurantImage(restaurantRepository, imageStorage);
  }

  @Bean
  public DeleteRestaurantImage deleteRestaurantImage(
      RestaurantRepository restaurantRepository, ImageStorage imageStorage) {
    return new DeleteRestaurantImage(restaurantRepository, imageStorage);
  }

  @Bean
  public SetRestaurantCoverImage setRestaurantCoverImage(
      RestaurantRepository restaurantRepository) {
    return new SetRestaurantCoverImage(restaurantRepository);
  }
}
