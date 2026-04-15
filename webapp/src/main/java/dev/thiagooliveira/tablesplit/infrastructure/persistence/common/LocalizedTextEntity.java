package dev.thiagooliveira.tablesplit.infrastructure.persistence.common;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "localized_texts")
public class LocalizedTextEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ElementCollection
  @CollectionTable(
      name = "localized_text_translations",
      joinColumns = @JoinColumn(name = "localized_text_id"))
  @MapKeyEnumerated(EnumType.STRING)
  @MapKeyColumn(name = "language", length = 5)
  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private Map<Language, String> translations = new HashMap<>();

  public static LocalizedTextEntity fromMap(Map<Language, String> map) {
    if (map == null) return null;
    LocalizedTextEntity entity = new LocalizedTextEntity();
    entity.setTranslations(new HashMap<>(map));
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LocalizedTextEntity that = (LocalizedTextEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Map<Language, String> getTranslations() {
    return translations;
  }

  public void setTranslations(Map<Language, String> translations) {
    this.translations = translations;
  }
}
