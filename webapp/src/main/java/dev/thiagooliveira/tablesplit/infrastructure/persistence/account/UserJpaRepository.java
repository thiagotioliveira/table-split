package dev.thiagooliveira.tablesplit.infrastructure.persistence.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByEmail(String email);

  @org.springframework.data.jpa.repository.Query(
      "SELECT u FROM UserEntity u WHERE REPLACE(u.phone, ' ', '') = REPLACE(:phone, ' ', '')")
  Optional<UserEntity> findByPhone(
      @org.springframework.data.repository.query.Param("phone") String phone);

  List<UserEntity> findByAccountId(UUID accountId);
}
