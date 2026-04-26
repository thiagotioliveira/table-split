package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ItemOption;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestionType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item_questions")
public class ItemQuestionEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false)
  private ItemEntity item;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 5)
  private Language language;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ItemQuestionType type;

  private boolean required;

  @Column(name = "min_selections")
  private Integer minSelections;

  @Column(name = "max_selections")
  private Integer maxSelections;

  @Convert(converter = OptionListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<ItemOption> options = new ArrayList<>();

  public static ItemQuestionEntity fromDomain(ItemQuestion domain, ItemEntity item, Language lang) {
    ItemQuestionEntity entity = new ItemQuestionEntity();
    entity.setId(domain.getId());
    entity.setItem(item);
    entity.setLanguage(lang);
    entity.setTitle(domain.getTitle());
    entity.setType(domain.getType());
    entity.setRequired(domain.isRequired());
    entity.setMinSelections(domain.getMinSelections());
    entity.setMaxSelections(domain.getMaxSelections());
    entity.setOptions(domain.getOptions());
    return entity;
  }

  public ItemQuestion toDomain() {
    ItemQuestion domain = new ItemQuestion();
    domain.setId(this.id);
    domain.setTitle(this.title);
    domain.setType(this.type);
    domain.setRequired(this.required);
    domain.setMinSelections(this.minSelections);
    domain.setMaxSelections(this.maxSelections);
    domain.setOptions(this.options);
    return domain;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ItemEntity getItem() {
    return item;
  }

  public void setItem(ItemEntity item) {
    this.item = item;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ItemQuestionType getType() {
    return type;
  }

  public void setType(ItemQuestionType type) {
    this.type = type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public Integer getMinSelections() {
    return minSelections;
  }

  public void setMinSelections(Integer minSelections) {
    this.minSelections = minSelections;
  }

  public Integer getMaxSelections() {
    return maxSelections;
  }

  public void setMaxSelections(Integer maxSelections) {
    this.maxSelections = maxSelections;
  }

  public List<ItemOption> getOptions() {
    return options;
  }

  public void setOptions(List<ItemOption> options) {
    this.options = options;
  }
}
