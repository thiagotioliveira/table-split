package dev.thiagooliveira.tablesplit.domain.vo;

public enum Language {
  PT("pt-BR"),
  EN("en-US");

  Language(String label) {
    this.label = label;
  }

  private final String label;

  public String getLabel() {
    return label;
  }
}
