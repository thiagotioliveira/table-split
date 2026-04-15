package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.CategoryDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

  @Query(
      "SELECT NEW dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.CategoryDto(c.id, c.restaurantId, c.numOrder, c.active, t, KEY(t)) "
          + "FROM CategoryEntity c JOIN c.name n JOIN n.translations t "
          + "WHERE c.restaurantId = :restaurantId AND KEY(t) IN :languages ORDER BY c.numOrder")
  List<CategoryDto> findAllByRestaurantIdAndLanguagesOrderByNumOrder(
      @Param("restaurantId") UUID restaurantId, @Param("languages") List<Language> languages);

  List<CategoryEntity> findByRestaurantId(UUID restaurantId);

  long countByRestaurantId(UUID restaurantId);

  long countByRestaurantIdAndActiveTrue(UUID restaurantId);

  long countByRestaurantIdAndActiveFalse(UUID restaurantId);
}
