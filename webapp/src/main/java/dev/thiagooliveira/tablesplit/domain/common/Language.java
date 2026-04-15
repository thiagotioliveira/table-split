package dev.thiagooliveira.tablesplit.domain.common;

import java.util.Locale;

public enum Language {
  PT("pt-PT"),
  EN("en-GB");

  Language(String label) {
    this.label = label;
  }

  private final String label;

  public String getLabel() {
    return label;
  }

  public static Language fromLocale(Locale locale) {
    if (locale == null || locale.getLanguage() == null) {
      return EN;
    }
    for (Language language : values()) {
      if (language.name().equalsIgnoreCase(locale.getLanguage())) {
        return language;
      }
    }
    return EN;
  }
}
