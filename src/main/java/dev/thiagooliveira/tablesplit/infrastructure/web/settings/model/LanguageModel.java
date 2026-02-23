package dev.thiagooliveira.tablesplit.infrastructure.web.settings.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.Language;

public class LanguageModel {
  private String label;
  private String code;

  public LanguageModel() {}

  public LanguageModel(Language language) {
    this.label = language.getLabel();
    this.code = language.getCode();
  }

  public Language toCommand() {
    return new Language(this.label, this.code);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
