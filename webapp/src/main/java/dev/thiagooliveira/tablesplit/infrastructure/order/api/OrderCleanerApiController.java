package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/orders/cleaner")
public class OrderCleanerApiController {

  private final OrderCleanerService orderCleanerService;

  public OrderCleanerApiController(OrderCleanerService orderCleanerService) {
    this.orderCleanerService = orderCleanerService;
  }

  @PostMapping("/run")
  public ResponseEntity<Void> runCleaner() {
    orderCleanerService.cleanOldOrders();
    return ResponseEntity.ok().build();
  }
}
