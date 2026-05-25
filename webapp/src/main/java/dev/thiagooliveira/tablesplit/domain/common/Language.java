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

  public static Locale toLocale(Language language) {
    if (language == PT) {
      return Locale.of("pt", "PT");
    }
    return Locale.ENGLISH;
  }

  public static Locale toLocale(String languageStr) {
    if (languageStr == null) {
      return Locale.ENGLISH;
    }
    try {
      return toLocale(Language.valueOf(languageStr.toUpperCase()));
    } catch (Exception e) {
      return Locale.ENGLISH;
    }
  }
}
