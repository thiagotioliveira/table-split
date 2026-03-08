package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import java.util.UUID;

public class ImageModel {
  private final String id;
  private final String url;

  public ImageModel(UUID id, String url) {
    this.id = id.toString();
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }
}
