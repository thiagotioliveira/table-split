package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.List;
import java.util.UUID;

public class ItemQuestion {
  private UUID id;
  private String title;
  private ItemQuestionType type;
  private boolean required;
  private Integer minSelections;
  private Integer maxSelections;
  private List<ItemOption> options;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
