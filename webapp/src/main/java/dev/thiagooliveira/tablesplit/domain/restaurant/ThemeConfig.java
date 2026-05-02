package dev.thiagooliveira.tablesplit.domain.restaurant;

public record ThemeConfig(
    String primary,
    String accent,
    String background,
    String card,
    String text,
    String fontDisplay,
    String fontBody,
    boolean dark,
    String textOnAccent,
    String textOnPrimary) {

  public static ThemeConfig resolve(Restaurant restaurant) {
    if (restaurant.getThemeName() == null || restaurant.getThemeName() == ThemeName.CUSTOM) {
      return new ThemeConfig(
          restaurant.getHashPrimaryColor() != null ? restaurant.getHashPrimaryColor() : "#c9a050",
          restaurant.getHashAccentColor() != null ? restaurant.getHashAccentColor() : "#FFEDD5",
          restaurant.getHashBackgroundColor() != null
              ? restaurant.getHashBackgroundColor()
              : "#faf8f5",
          restaurant.getHashCardColor() != null ? restaurant.getHashCardColor() : "#ffffff",
          restaurant.getHashTextColor() != null ? restaurant.getHashTextColor() : "#1a1714",
          "'Cormorant Garamond', serif",
          "'Inter', sans-serif",
          false,
          "#000000",
          "#ffffff");
    }

    return switch (restaurant.getThemeName()) {
      case DARK_ELEGANCE ->
          new ThemeConfig(
              "#e5b869", "#2d2a26", "#121212", "#1e1e1e", "#f5f5f5", "Outfit", "Inter", true,
              "#ffffff", "#121212");
      case OCEAN ->
          new ThemeConfig(
              "#0ea5e9", "#e0f2fe", "#f8fafc", "#ffffff", "#0f172a", "Outfit", "Inter", false,
              "#0f172a", "#ffffff");
      case NATURE ->
          new ThemeConfig(
              "#2d5a47",
              "#e6f4ea",
              "#f5fff7",
              "#ffffff",
              "#1e3a2a",
              "Quicksand",
              "Inter",
              false,
              "#1e3a2a",
              "#ffffff");
      case MINIMAL ->
          new ThemeConfig(
              "#000000", "#f1f5f9", "#ffffff", "#ffffff", "#000000", "Inter", "Inter", false,
              "#000000", "#ffffff");
      case SOFT_LAVENDER ->
          new ThemeConfig(
              "#9061F9", "#F5F3FF", "#FDFDFF", "#FFFFFF", "#441D8B", "Outfit", "Inter", false,
              "#441D8B", "#ffffff");
      case MINT_FRESH ->
          new ThemeConfig(
              "#10B981",
              "#ECFDF5",
              "#F9FEFB",
              "#FFFFFF",
              "#064E3B",
              "Montserrat",
              "Inter",
              false,
              "#064E3B",
              "#ffffff");
      case PEACH_BLOSSOM ->
          new ThemeConfig(
              "#FB7185",
              "#FFF1F2",
              "#FFFBFC",
              "#FFFFFF",
              "#881337",
              "Quicksand",
              "Inter",
              false,
              "#881337",
              "#ffffff");
      case SKY_PASTEL ->
          new ThemeConfig(
              "#3EB0EF", "#F0F9FF", "#F9FDFF", "#FFFFFF", "#0C4A6E", "Outfit", "Inter", false,
              "#0C4A6E", "#ffffff");
      case VIBRANT_ORANGE ->
          new ThemeConfig(
              "#f97316", "#10b981", "#ffffff", "#ffffff", "#0f172a", "Outfit", "Inter", false,
              "#ffffff", "#ffffff");
      case VIBRANT_ORANGE_DARK ->
          new ThemeConfig(
              "#f97316", "#10b981", "#111827", "#1f2937", "#f9fafb", "Outfit", "Inter", true,
              "#ffffff", "#ffffff");
      case UBER_STYLE ->
          new ThemeConfig(
              "#06C167", "#000000", "#ffffff", "#ffffff", "#000000", "Outfit", "Inter", false,
              "#ffffff", "#ffffff");
      case UBER_STYLE_DARK ->
          new ThemeConfig(
              "#06C167", "#ffffff", "#000000", "#121212", "#ffffff", "Outfit", "Inter", true,
              "#000000", "#ffffff");
      case NICOLAU_STYLE ->
          new ThemeConfig(
              "#00b2bd",
              "#f2a9b1",
              "#fcfbf7",
              "#ffffff",
              "#1a3a3a",
              "Cormorant Garamond",
              "Montserrat",
              false,
              "#1a3a3a",
              "#ffffff");
      case TROPICAL_SOUL ->
          new ThemeConfig(
              "#009c3b",
              "#ffdf00",
              "#ffffff",
              "#f8fff9",
              "#002776",
              "Montserrat",
              "Inter",
              false,
              "#002776",
              "#ffffff");
      case ARTISAN_ROAST ->
          new ThemeConfig(
              "#4b2c20",
              "#d4a373",
              "#fefae0",
              "#ffffff",
              "#3d2b1f",
              "Cormorant Garamond",
              "Inter",
              false,
              "#ffffff",
              "#ffffff");
      case GOLDEN_HERITAGE ->
          new ThemeConfig(
              "#c9a050", "#FFEDD5", "#faf8f5", "#ffffff", "#1a1714", "Outfit", "Inter", false,
              "#1a1714", "#ffffff");
      default ->
          new ThemeConfig(
              "#c9a050", "#FFEDD5", "#faf8f5", "#ffffff", "#1a1714", "Outfit", "Inter", false,
              "#1a1714", "#ffffff");
    };
  }
}
