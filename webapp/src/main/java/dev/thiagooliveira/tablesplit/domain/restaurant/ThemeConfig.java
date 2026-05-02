package dev.thiagooliveira.tablesplit.domain.restaurant;

public record ThemeConfig(
    String primary,
    String accent,
    String background,
    String card,
    String text,
    String fontDisplay,
    String fontBody,
    boolean dark) {

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
          false);
    }

    return switch (restaurant.getThemeName()) {
      case DARK_ELEGANCE ->
          new ThemeConfig(
              "#e5b869", "#2d2a26", "#121212", "#1e1e1e", "#f5f5f5", "Outfit", "Inter", true);
      case OCEAN ->
          new ThemeConfig(
              "#0ea5e9", "#e0f2fe", "#f8fafc", "#ffffff", "#0f172a", "Outfit", "Inter", false);
      case NATURE ->
          new ThemeConfig(
              "#2d5a47", "#e6f4ea", "#f5fff7", "#ffffff", "#1e3a2a", "Quicksand", "Inter", false);
      case MINIMAL ->
          new ThemeConfig(
              "#000000", "#f1f5f9", "#ffffff", "#ffffff", "#000000", "Inter", "Inter", false);
      case SOFT_LAVENDER ->
          new ThemeConfig(
              "#9061F9", "#F5F3FF", "#FDFDFF", "#FFFFFF", "#441D8B", "Outfit", "Inter", false);
      case MINT_FRESH ->
          new ThemeConfig(
              "#10B981", "#ECFDF5", "#F9FEFB", "#FFFFFF", "#064E3B", "Montserrat", "Inter", false);
      case PEACH_BLOSSOM ->
          new ThemeConfig(
              "#FB7185", "#FFF1F2", "#FFFBFC", "#FFFFFF", "#881337", "Quicksand", "Inter", false);
      case SKY_PASTEL ->
          new ThemeConfig(
              "#3EB0EF", "#F0F9FF", "#F9FDFF", "#FFFFFF", "#0C4A6E", "Outfit", "Inter", false);
      case VIBRANT_ORANGE ->
          new ThemeConfig(
              "#f97316", "#10b981", "#ffffff", "#ffffff", "#0f172a", "Outfit", "Inter", false);
      case VIBRANT_ORANGE_DARK ->
          new ThemeConfig(
              "#f97316", "#10b981", "#111827", "#1f2937", "#f9fafb", "Outfit", "Inter", true);
      case UBER_STYLE ->
          new ThemeConfig(
              "#06C167", "#000000", "#ffffff", "#ffffff", "#000000", "Outfit", "Inter", false);
      case UBER_STYLE_DARK ->
          new ThemeConfig(
              "#06C167", "#ffffff", "#000000", "#121212", "#ffffff", "Outfit", "Inter", true);
      case NICOLAU_STYLE ->
          new ThemeConfig(
              "#00b2bd",
              "#f2a9b1",
              "#fcfbf7",
              "#ffffff",
              "#1a3a3a",
              "Cormorant Garamond",
              "Montserrat",
              false);
      case GOLDEN_HERITAGE ->
          new ThemeConfig(
              "#c9a050", "#FFEDD5", "#faf8f5", "#ffffff", "#1a1714", "Outfit", "Inter", false);
      default ->
          new ThemeConfig(
              "#c9a050", "#FFEDD5", "#faf8f5", "#ffffff", "#1a1714", "Outfit", "Inter", false);
    };
  }
}
