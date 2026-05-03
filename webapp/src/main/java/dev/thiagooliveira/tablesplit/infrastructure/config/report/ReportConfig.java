package dev.thiagooliveira.tablesplit.infrastructure.config.report;

import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import dev.thiagooliveira.tablesplit.domain.order.FeedbackRepository;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {

  @Bean
  public GetReportsOverview getReportsOverview(
      OrderRepository orderRepository,
      FeedbackRepository feedbackRepository,
      TableRepository tableRepository,
      ItemRepository itemRepository,
      PromotionRepository promotionRepository) {
    return new GetReportsOverview(
        orderRepository, feedbackRepository, tableRepository, itemRepository, promotionRepository);
  }
}
