package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemQuestionJpaRepository extends JpaRepository<ItemQuestionEntity, UUID> {
  List<ItemQuestionEntity> findByItemIdInAndLanguageIn(
      Collection<UUID> itemIds, Collection<Language> languages);
}
