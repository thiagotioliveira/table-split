package dev.thiagooliveira.tablesplit.domain.restaurant;

public enum AveragePrice {
  PRICE_5_20("5-20"),
  PRICE_20_50("20-50"),
  PRICE_50_100("50-100"),
  PRICE_100_150("100-150");

  private final String label;

  AveragePrice(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
