package dev.thiagooliveira.tablesplit.application.notification;

import dev.thiagooliveira.tablesplit.domain.notification.WaiterCall;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaiterCallRepository {
  void save(WaiterCall waiterCall);

  List<WaiterCall> findAllActiveByRestaurantId(UUID restaurantId);

  Optional<WaiterCall> findById(UUID id);

  Optional<WaiterCall> findActiveByRestaurantIdAndTableCod(UUID restaurantId, String tableCod);
}
