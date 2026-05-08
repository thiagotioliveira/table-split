package dev.thiagooliveira.tablesplit.application.restaurant.exception;

import dev.thiagooliveira.tablesplit.application.exception.ApplicationException;

public class ImageSizeExceededException extends ApplicationException {
  public ImageSizeExceededException(String message) {
    super(message);
  }
}
