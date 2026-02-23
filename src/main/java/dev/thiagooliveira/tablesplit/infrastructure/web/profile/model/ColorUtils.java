package dev.thiagooliveira.tablesplit.infrastructure.web.profile.model;

public class ColorUtils {

  public static String darkenAndConvertToRgba(String hex, double factor, double alpha) {
    hex = hex.replace("#", "");

    int r = (int) (Integer.parseInt(hex.substring(0, 2), 16) * (1 - factor));
    int g = (int) (Integer.parseInt(hex.substring(2, 4), 16) * (1 - factor));
    int b = (int) (Integer.parseInt(hex.substring(4, 6), 16) * (1 - factor));

    return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, alpha);
  }

  public static String lighten(String hexColor, double percentage) {
    java.awt.Color color = java.awt.Color.decode(hexColor);

    float[] hsl = rgbToHsl(color);

    // aumenta luminosidade
    hsl[2] = Math.min(1f, hsl[2] + (float) percentage);

    java.awt.Color lighterColor = hslToRgb(hsl);

    return String.format(
        "#%02X%02X%02X", lighterColor.getRed(), lighterColor.getGreen(), lighterColor.getBlue());
  }

  private static float[] rgbToHsl(java.awt.Color color) {
    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;

    float max = Math.max(r, Math.max(g, b));
    float min = Math.min(r, Math.min(g, b));
    float h, s, l = (max + min) / 2f;

    if (max == min) {
      h = s = 0f;
    } else {
      float d = max - min;
      s = l > 0.5f ? d / (2f - max - min) : d / (max + min);

      if (max == r) {
        h = (g - b) / d + (g < b ? 6f : 0f);
      } else if (max == g) {
        h = (b - r) / d + 2f;
      } else {
        h = (r - g) / d + 4f;
      }

      h /= 6f;
    }

    return new float[] {h, s, l};
  }

  private static java.awt.Color hslToRgb(float[] hsl) {
    float h = hsl[0];
    float s = hsl[1];
    float l = hsl[2];

    float r, g, b;

    if (s == 0f) {
      r = g = b = l;
    } else {
      float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
      float p = 2 * l - q;

      r = hueToRgb(p, q, h + 1f / 3f);
      g = hueToRgb(p, q, h);
      b = hueToRgb(p, q, h - 1f / 3f);
    }

    return new java.awt.Color(clamp(r), clamp(g), clamp(b));
  }

  private static float hueToRgb(float p, float q, float t) {
    if (t < 0) t += 1;
    if (t > 1) t -= 1;
    if (t < 1f / 6f) return p + (q - p) * 6f * t;
    if (t < 1f / 2f) return q;
    if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
    return p;
  }

  private static int clamp(float value) {
    return Math.round(Math.min(1f, Math.max(0f, value)) * 255);
  }
}
