package dev.thiagooliveira.tablesplit.infrastructure.persistence.notification;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.notification.PushSubscription;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PushSubscriptionEntityMapper {

  PushSubscription toDomain(PushSubscriptionEntity entity);

  PushSubscriptionEntity toEntity(PushSubscription domain);

  @AfterMapping
  default void setDefaultCreatedAt(
      PushSubscription domain, @MappingTarget PushSubscriptionEntity entity) {
    if (entity.getCreatedAt() == null) {
      entity.setCreatedAt(Time.now());
    }
  }
}
