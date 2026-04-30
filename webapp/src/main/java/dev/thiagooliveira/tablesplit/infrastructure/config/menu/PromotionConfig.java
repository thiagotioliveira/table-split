package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionConfig {

  @Bean
  public CreatePromotion createPromotion(
      PromotionRepository promotionRepository, PlanLimitValidator planLimitValidator) {
    return new CreatePromotion(promotionRepository, planLimitValidator);
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
