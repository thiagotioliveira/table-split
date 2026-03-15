package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.ItemProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, UUID> {

  @EntityGraph(attributePaths = "images")
  @Query(
      "SELECT NEW dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.ItemProjection("
          + "i, tn, td, KEY(tn)) "
          + "FROM ItemEntity i "
          + "JOIN i.category c "
          + "LEFT JOIN i.name n "
          + "LEFT JOIN n.translations tn ON KEY(tn) IN :languages "
          + "LEFT JOIN i.description d "
          + "LEFT JOIN d.translations td ON KEY(td) = KEY(tn) "
          + "WHERE c.restaurantId = :restaurantId AND KEY(tn) IN :languages")
  List<ItemProjection> findAllByCategoryRestaurantIdAndLanguages(
      @Param("restaurantId") UUID restaurantId, @Param("languages") List<Language> languages);

  long countByCategoryRestaurantId(UUID restaurantId);

  long countByCategoryRestaurantIdAndActiveTrue(UUID restaurantId);

  long countByCategoryRestaurantIdAndActiveFalse(UUID restaurantId);
}
