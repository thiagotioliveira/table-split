package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private List<ApiFieldError> errors;

  public ApiErrorResponse() {}

  public ApiErrorResponse(int status, String error, String message, List<ApiFieldError> errors) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.message = message;
    this.errors = errors;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ApiFieldError> getErrors() {
    return errors;
  }

  public void setErrors(List<ApiFieldError> errors) {
    this.errors = errors;
  }

  public static class ApiFieldError {
    private String field;
    private String message;
    private Object rejectedValue;

    public ApiFieldError() {}

    public ApiFieldError(String field, String message, Object rejectedValue) {
      this.field = field;
      this.message = message;
      this.rejectedValue = rejectedValue;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Object getRejectedValue() {
      return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
      this.rejectedValue = rejectedValue;
    }
  }
}
