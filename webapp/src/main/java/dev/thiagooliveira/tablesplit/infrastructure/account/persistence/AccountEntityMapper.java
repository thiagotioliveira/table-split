package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountEntityMapper {

  Account toDomain(AccountEntity entity);

  AccountEntity toEntity(Account domain);
}
