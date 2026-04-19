package dev.thiagooliveira.tablesplit.infrastructure.web.notification;

import dev.thiagooliveira.tablesplit.application.notification.SseService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications/sse")
public class SseNotificationController {

  private final SseService sseService;

  public SseNotificationController(SseService sseService) {
    this.sseService = sseService;
  }

  @GetMapping("/subscribe/{restaurantId}")
  public SseEmitter subscribe(@PathVariable UUID restaurantId) {
    return sseService.subscribe(restaurantId);
  }
}
