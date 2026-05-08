package dev.thiagooliveira.tablesplit.application.menu.command;

public record ItemImageDataCommand(String fileName, String contentType, byte[] content) {
  public dev.thiagooliveira.tablesplit.domain.menu.ItemImageData toDomain() {
    return new dev.thiagooliveira.tablesplit.domain.menu.ItemImageData(
        fileName, contentType, content);
  }
}
