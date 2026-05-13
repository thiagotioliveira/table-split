package dev.thiagooliveira.tablesplit.domain.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public enum Currency {
  EUR("€"),
  BRL("R$");

  private final String symbol;

  private Currency(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public String format(BigDecimal value, Language language) {
    if (value == null) return "";
    Locale locale = Locale.forLanguageTag(language.getLabel());
    NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
    formatter.setCurrency(java.util.Currency.getInstance(this.name()));
    return formatter.format(value);
  }
}
