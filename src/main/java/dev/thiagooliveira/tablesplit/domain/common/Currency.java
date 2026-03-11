package dev.thiagooliveira.tablesplit.domain.common;

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
}
