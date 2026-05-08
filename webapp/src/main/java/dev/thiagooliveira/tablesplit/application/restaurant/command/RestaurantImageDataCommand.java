package dev.thiagooliveira.tablesplit.application.restaurant.command;

public record RestaurantImageDataCommand(String fileName, String contentType, byte[] content) {
  public dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageData toDomain() {
    return new dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageData(
        fileName, contentType, content);
  }
}
