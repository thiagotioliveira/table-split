package dev.thiagooliveira.tablesplit.domain.common;

import java.util.List;
import java.util.function.Function;

public record Pagination<T>(
    List<T> items, int currentPage, int totalPages, long totalElements, int size, boolean hasNext) {

  public <U> Pagination<U> map(Function<? super T, ? extends U> converter) {
    List<U> convertedItems = (List<U>) this.items.stream().map(converter).toList();
    return new Pagination<>(convertedItems, currentPage, totalPages, totalElements, size, hasNext);
  }
}
