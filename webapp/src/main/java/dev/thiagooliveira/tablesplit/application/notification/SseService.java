package dev.thiagooliveira.tablesplit.application.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
  private static final Logger logger = LoggerFactory.getLogger(SseService.class);
  private final Map<UUID, List<SseEmitter>> restaurantEmitters = new ConcurrentHashMap<>();

  public SseEmitter subscribe(UUID restaurantId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    List<SseEmitter> emitters =
        restaurantEmitters.computeIfAbsent(restaurantId, k -> new CopyOnWriteArrayList<>());
    emitters.add(emitter);
    logger.debug(
        "New SSE subscription for restaurant: {}. Total emitters: {}",
        restaurantId,
        emitters.size());

    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError((e) -> emitters.remove(emitter));

    // Send check-in event
    try {
      emitter.send(SseEmitter.event().name("init").data("connected"));
    } catch (IOException e) {
      emitters.remove(emitter);
    }

    return emitter;
  }

  public void broadcast(UUID restaurantId, Object payload) {
    List<SseEmitter> emitters = restaurantEmitters.get(restaurantId);
    if (emitters != null) {
      logger.debug(
          "Broadcasting event to {} emitters for restaurant: {}", emitters.size(), restaurantId);
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(payload);
        } catch (IOException e) {
          logger.debug("Removing broken SSE emitter for restaurant: {}", restaurantId);
          emitters.remove(emitter);
        }
      }
    } else {
      logger.debug("No active SSE emitters found for restaurant: {}", restaurantId);
    }
  }
}
