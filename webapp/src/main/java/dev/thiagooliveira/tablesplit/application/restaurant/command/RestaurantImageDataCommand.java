package dev.thiagooliveira.tablesplit.application.restaurant.command;

public record RestaurantImageDataCommand(String fileName, String contentType, byte[] content) {
  public dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageData toDomain() {
    return new dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantImageData(
        fileName, contentType, content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RestaurantImageDataCommand that = (RestaurantImageDataCommand) o;
    return java.util.Objects.equals(fileName, that.fileName)
        && java.util.Objects.equals(contentType, that.contentType)
        && java.util.Arrays.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    int result = java.util.Objects.hash(fileName, contentType);
    result = 31 * result + java.util.Arrays.hashCode(content);
    return result;
  }

  @Override
  public String toString() {
    return "RestaurantImageDataCommand["
        + "fileName="
        + fileName
        + ", "
        + "contentType="
        + contentType
        + ", "
        + "content="
        + java.util.Arrays.toString(content)
        + "]";
  }
}
