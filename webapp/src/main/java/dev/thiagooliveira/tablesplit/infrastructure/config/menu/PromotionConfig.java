package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionConfig {

  @Bean
  public CreatePromotion createPromotion(PromotionRepository promotionRepository) {
    return new CreatePromotion(promotionRepository);
  }

  @Bean
  public UpdatePromotion updatePromotion(PromotionRepository promotionRepository) {
    return new UpdatePromotion(promotionRepository);
  }

  @Bean
  public DeletePromotion deletePromotion(PromotionRepository promotionRepository) {
    return new DeletePromotion(promotionRepository);
  }

  @Bean
  public GetPromotions getPromotions(PromotionRepository promotionRepository) {
    return new GetPromotions(promotionRepository);
  }

  @Bean
  public TogglePromotion togglePromotion(PromotionRepository promotionRepository) {
    return new TogglePromotion(promotionRepository);
  }
}
