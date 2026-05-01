package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import org.springframework.stereotype.Component;

@Component
public class WaiterCallEntityMapper {

  public WaiterCall toDomain(WaiterCallEntity entity) {
    return new WaiterCall(
        entity.getId(),
        entity.getRestaurantId(),
        entity.getTableCod(),
        entity.getCreatedAt(),
        entity.getDismissedAt(),
        entity.getCallCount());
  }

  public WaiterCallEntity toEntity(WaiterCall domain) {
    return new WaiterCallEntity(
        domain.getId(),
        domain.getRestaurantId(),
        domain.getTableCod(),
        domain.getCreatedAt(),
        domain.getDismissedAt(),
        domain.getCallCount());
  }
}
