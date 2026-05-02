package dev.thiagooliveira.tablesplit.domain.restaurant;

public enum ThemeName {
  DEFAULT("Default Theme"),
  DARK_ELEGANCE("Dark Elegance"),
  OCEAN("Ocean"),
  NATURE("Nature"),
  MINIMAL("Minimalist"),
  SOFT_LAVENDER("Soft Lavender"),
  MINT_FRESH("Mint Fresh"),
  PEACH_BLOSSOM("Peach Blossom"),
  SKY_PASTEL("Sky Pastel"),
  VIBRANT_ORANGE("Vibrant Orange"),
  CUSTOM("Custom (Your Brand Colors)");

  private final String displayName;

  ThemeName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
