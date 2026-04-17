package dev.thiagooliveira.tablesplit.agent.listener;

import dev.thiagooliveira.tablesplit.agent.model.IntegrationOrderDTO;
import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

  private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
  private final PrinterService printerService;

  public OrderListener(PrinterService printerService) {
    this.printerService = printerService;
  }

  /**
   * This listener binds to a queue that listens to ALL restaurant orders. In a real agent, the
   * queue name would be specific to ONE restaurant.
   */
  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "pos.integration.generic.queue", durable = "true"),
              exchange = @Exchange(value = "order.integration.exchange", type = "topic"),
              key = "restaurant.*.orders"))
  public void receiveOrder(IntegrationOrderDTO order) {
    log.info("Order for Table {} received successfully. Sending to printer...", order.tableCod());
    printerService.printOrder(order);
  }
}
