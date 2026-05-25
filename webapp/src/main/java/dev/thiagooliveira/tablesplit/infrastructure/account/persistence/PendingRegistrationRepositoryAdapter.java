package dev.thiagooliveira.tablesplit.infrastructure.account.persistence;

import dev.thiagooliveira.tablesplit.domain.account.PendingRegistration;
import dev.thiagooliveira.tablesplit.domain.account.PendingRegistrationRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PendingRegistrationRepositoryAdapter implements PendingRegistrationRepository {

  private final PendingRegistrationJpaRepository jpaRepository;

  public PendingRegistrationRepositoryAdapter(PendingRegistrationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @Transactional
  public void save(PendingRegistration pendingRegistration) {
    PendingRegistrationEntity entity =
        jpaRepository
            .findByEmail(pendingRegistration.getEmail())
            .orElseGet(PendingRegistrationEntity::new);

    entity.setId(pendingRegistration.getId());
    entity.setEmail(pendingRegistration.getEmail());
    entity.setCode(pendingRegistration.getCode());
    entity.setRegistrationData(pendingRegistration.getRegistrationData());
    entity.setExpiresAt(pendingRegistration.getExpiresAt());
    entity.setLanguage(pendingRegistration.getLanguage());

    jpaRepository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PendingRegistration> findByEmail(String email) {
    return jpaRepository
        .findByEmail(email)
        .map(
            entity ->
                new PendingRegistration(
                    entity.getId(),
                    entity.getEmail(),
                    entity.getCode(),
                    entity.getRegistrationData(),
                    entity.getExpiresAt(),
                    entity.getLanguage()));
  }

  @Override
  @Transactional
  public void deleteByEmail(String email) {
    jpaRepository.deleteByEmail(email);
  }
}
