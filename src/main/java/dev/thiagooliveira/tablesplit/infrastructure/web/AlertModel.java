package dev.thiagooliveira.tablesplit.infrastructure.web;

public class AlertModel {
  private final String message;
  private final String type;

  public AlertModel(String message, String type) {
    this.message = message;
    this.type = type;
  }

  public static AlertModel success(String message) {
    return new AlertModel(message, "success");
  }

  public static AlertModel error(String message) {
    return new AlertModel(message, "danger");
  }

  public static AlertModel warning(String message) {
    return new AlertModel(message, "warning");
  }

  public String getMessage() {
    return message;
  }

  public String getType() {
    return type;
  }
}
