package dev.thiagooliveira.tablesplit.infrastructure.config.order;

import dev.thiagooliveira.tablesplit.application.order.RateItem;
import dev.thiagooliveira.tablesplit.application.order.SubmitGeneralFeedback;
import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackConfig {

  @Bean
  public SubmitGeneralFeedback submitGeneralFeedback(
      FeedbackRepository feedbackRepository,
      dev.thiagooliveira.tablesplit.domain.order.OrderRepository orderRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher) {
    return new SubmitGeneralFeedback(feedbackRepository, orderRepository, eventPublisher);
  }

  @Bean
  public RateItem rateItem(FeedbackRepository feedbackRepository) {
    return new RateItem(feedbackRepository);
  }

  @Bean
  public dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview getFeedbackOverview(
      FeedbackRepository feedbackRepository) {
    return new dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview(
        feedbackRepository);
  }
}
