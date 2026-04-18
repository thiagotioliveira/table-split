package dev.thiagooliveira.tablesplit.infrastructure.web.cleaner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/cleaner")
public class OrderCleanerController {

  private final OrderCleanerService orderCleanerService;

  public OrderCleanerController(OrderCleanerService orderCleanerService) {
    this.orderCleanerService = orderCleanerService;
  }

  @PostMapping("/run")
  public ResponseEntity<Void> runCleaner() {
    orderCleanerService.cleanOldOrders();
    return ResponseEntity.ok().build();
  }
}
