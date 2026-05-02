package dev.thiagooliveira.tablesplit.infrastructure.security.context;

import dev.thiagooliveira.tablesplit.domain.restaurant.ThemeConfig;

public record ThemeContext(
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
  public static ThemeContext from(ThemeConfig config) {
    if (config == null) return null;
    return new ThemeContext(
        config.primary(),
        config.accent(),
        config.background(),
        config.card(),
        config.text(),
        config.fontDisplay(),
        config.fontBody(),
        config.dark(),
        config.textOnAccent(),
        config.textOnPrimary());
  }
}
