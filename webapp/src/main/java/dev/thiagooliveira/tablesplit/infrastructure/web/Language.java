package dev.thiagooliveira.tablesplit.infrastructure.web;

import java.util.Locale;

public enum Language {
  PT("pt-PT", "🇵🇹", "language.PT"),
  EN("en-GB", "🇬🇧", "language.EN");

  Language(String label, String flag, String name) {
    this.label = label;
    this.flag = flag;
    this.name = name;
  }

  private final String label;
  private final String flag;
  private final String name;

  public String getLabel() {
    return label;
  }

  public String getFlag() {
    return flag;
  }

  public String getName() {
    return name;
  }

  public dev.thiagooliveira.tablesplit.domain.common.Language toDomain() {
    return dev.thiagooliveira.tablesplit.domain.common.Language.valueOf(this.name());
  }

  public static Language fromDomain(dev.thiagooliveira.tablesplit.domain.common.Language domain) {
    return Language.valueOf(domain.name());
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
