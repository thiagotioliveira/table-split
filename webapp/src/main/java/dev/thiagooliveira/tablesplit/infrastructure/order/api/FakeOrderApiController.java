package dev.thiagooliveira.tablesplit.infrastructure.order.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/orders/fake")
public class FakeOrderApiController {

  private final FakeOrderService fakeOrderService;

  public FakeOrderApiController(FakeOrderService fakeOrderService) {
    this.fakeOrderService = fakeOrderService;
  }

  @PostMapping("/run")
  public ResponseEntity<Void> runFakeOrderGeneration() {
    fakeOrderService.generateFakeOrder();
    return ResponseEntity.ok().build();
  }
}
