package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.application.order.RateItem;
import dev.thiagooliveira.tablesplit.application.order.SubmitGeneralFeedback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackConfig {

  @Bean
  public SubmitGeneralFeedback submitGeneralFeedback(FeedbackRepository feedbackRepository) {
    return new SubmitGeneralFeedback(feedbackRepository);
  }

  @Bean
  public RateItem rateItem(FeedbackRepository feedbackRepository) {
    return new RateItem(feedbackRepository);
  }
}
