package dev.thiagooliveira.tablesplit.domain.restaurant;

public class Tag {

  private String icon;
  private String description;

  public Tag() {}

  public Tag(String icon, String description) {
    this.icon = icon;
    this.description = description;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
