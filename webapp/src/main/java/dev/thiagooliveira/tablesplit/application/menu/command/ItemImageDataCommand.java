package dev.thiagooliveira.tablesplit.application.menu.command;

public record ItemImageDataCommand(String fileName, String contentType, byte[] content) {
  public dev.thiagooliveira.tablesplit.domain.menu.ItemImageData toDomain() {
    return new dev.thiagooliveira.tablesplit.domain.menu.ItemImageData(
        fileName, contentType, content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ItemImageDataCommand that = (ItemImageDataCommand) o;
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
    return "ItemImageDataCommand["
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
