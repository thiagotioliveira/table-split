package dev.thiagooliveira.tablesplit.infrastructure.config.restaurant;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.restaurant.*;
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
      RestaurantRepository restaurantRepository,
      ImageStorage imageStorage,
      @org.springframework.beans.factory.annotation.Value("${app.gallery.image.max-size:1048576}")
          long maxImageSize) {
    return new UploadRestaurantImage(restaurantRepository, imageStorage, maxImageSize);
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

  @Bean
  public GetOrCreateToken getOrCreateToken(
      PrintAgentTokenRepository tokenRepository, RestaurantRepository restaurantRepository) {
    return new GetOrCreateToken(tokenRepository, restaurantRepository);
  }

  @Bean
  public RegenerateToken regenerateToken(
      PrintAgentTokenRepository tokenRepository, RestaurantRepository restaurantRepository) {
    return new RegenerateToken(tokenRepository, restaurantRepository);
  }

  @Bean
  public ValidateAndUseToken validateAndUseToken(PrintAgentTokenRepository tokenRepository) {
    return new ValidateAndUseToken(tokenRepository);
  }
}
