package dev.thiagooliveira.tablesplit.infrastructure.web;

public enum RestaurantTag {
  WIFI("tags.wifi", "📶"),
  WINE("tags.wine", "🍷"),
  PARKING("tags.parking", "🅿️"),
  ACCESSIBLE("tags.accessible", "♿️"),
  DELIVERY("tags.delivery", "🛵️"),
  RESERVATIONS("tags.reservations", "📅️"),
  KIDS_SPACE("tags.kidsspace", "👶"),
  LIVE_MUSIC("tags.livemusic", "🎵"),
  PET_FRIENDLY("tags.petfriendly", "🐕"),
  AIR_CONDITIONING("tags.air", "❄️"),
  VEGAN("tags.vegan", "🌱"),
  GLUTEN_FREE("tags.glutenfree", "🌾"),
  ROMANTIC("tags.romantic", "💑"),
  GROUPS("tags.groups", "👥"),
  CARDS("tags.cards", "💳");

  private final String label;
  private final String icon;

  RestaurantTag(String label, String icon) {
    this.label = label;
    this.icon = icon;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }
}
