package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffEntityMapper {

  Staff toDomain(StaffEntity entity);

  StaffEntity toEntity(Staff domain);
}
