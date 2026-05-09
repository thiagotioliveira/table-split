package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.demo.fake-orders")
public class FakeOrderProperties {

  private List<String> customerNames;
  private Map<Integer, List<String>> feedbacks;

  public List<String> getCustomerNames() {
    return customerNames;
  }

  public void setCustomerNames(List<String> customerNames) {
    this.customerNames = customerNames;
  }

  public Map<Integer, List<String>> getFeedbacks() {
    return feedbacks;
  }

  public void setFeedbacks(Map<Integer, List<String>> feedbacks) {
    this.feedbacks = feedbacks;
  }
}
