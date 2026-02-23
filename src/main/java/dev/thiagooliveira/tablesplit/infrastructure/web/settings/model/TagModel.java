package dev.thiagooliveira.tablesplit.infrastructure.web.settings.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;

public class TagModel {

  private String icon;
  private String description;

  public TagModel() {}

  public TagModel(Tag tag) {
    this.icon = tag.getIcon();
    this.description = tag.getDescription();
  }

  public Tag toCommand() {
    return new Tag(this.icon, this.description);
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
