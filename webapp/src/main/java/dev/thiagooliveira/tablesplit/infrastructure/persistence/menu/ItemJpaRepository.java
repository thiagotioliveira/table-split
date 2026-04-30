package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.ItemProjection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, UUID> {

  @EntityGraph(
      attributePaths =
          "images") // We can't easily join questions here for a Map without fetching all of them
  // unless we use a filter
  @Query(
      "SELECT NEW dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.dto.ItemProjection("
          + "i, tn, td, KEY(tn)) "
          + "FROM ItemEntity i "
          + "JOIN i.category c "
          + "LEFT JOIN i.name n "
          + "LEFT JOIN n.translations tn ON KEY(tn) IN :languages "
          + "LEFT JOIN i.description d "
          + "LEFT JOIN d.translations td ON KEY(td) = KEY(tn) "
          + "WHERE c.restaurantId = :restaurantId AND KEY(tn) IN :languages AND i.deletedAt IS NULL")
  List<ItemProjection> findAllByCategoryRestaurantIdAndLanguages(
      @Param("restaurantId") UUID restaurantId, @Param("languages") List<Language> languages);

  @Query(
      "SELECT i FROM ItemEntity i JOIN FETCH i.category c JOIN FETCH c.restaurant WHERE c.restaurantId = :restaurantId AND i.deletedAt IS NULL")
  List<ItemEntity> findByCategoryRestaurantIdAndDeletedAtIsNull(
      @Param("restaurantId") UUID restaurantId);

  @Query(
      "SELECT i FROM ItemEntity i JOIN FETCH i.category c JOIN FETCH c.restaurant WHERE i.id = :id AND i.deletedAt IS NULL")
  Optional<ItemEntity> findByIdAndDeletedAtIsNull(@Param("id") UUID id);

  @Query(
      "SELECT COUNT(i) FROM ItemEntity i WHERE i.category.restaurantId = :restaurantId AND i.deletedAt IS NULL")
  long countByCategoryRestaurantId(@Param("restaurantId") UUID restaurantId);

  @Query(
      "SELECT COUNT(i) FROM ItemEntity i WHERE i.category.restaurantId = :restaurantId AND i.active = true AND i.deletedAt IS NULL")
  long countByCategoryRestaurantIdAndActiveTrue(@Param("restaurantId") UUID restaurantId);

  @Query(
      "SELECT COUNT(i) FROM ItemEntity i WHERE i.category.restaurantId = :restaurantId AND i.active = false AND i.deletedAt IS NULL")
  long countByCategoryRestaurantIdAndActiveFalse(@Param("restaurantId") UUID restaurantId);

  @Query("SELECT COUNT(ti) > 0 FROM TicketItemEntity ti WHERE ti.itemId = :itemId")
  boolean existsInTicketItems(@Param("itemId") UUID itemId);
}
