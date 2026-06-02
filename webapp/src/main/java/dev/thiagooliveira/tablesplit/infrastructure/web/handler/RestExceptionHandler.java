package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.application.account.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.ForbiddenException;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(RestExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<ApiErrorResponse.ApiFieldError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                err ->
                    new ApiErrorResponse.ApiFieldError(
                        err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
            .collect(Collectors.toList());

    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Erro de validação nos campos informados.",
            fieldErrors);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException ex) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            null);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiErrorResponse> handleForbiddenException(ForbiddenException ex) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            ex.getMessage(),
            null);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(PlanLimitExceededException.class)
  public ResponseEntity<ApiErrorResponse> handlePlanLimitExceededException(
      PlanLimitExceededException ex) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            null);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(TableAlreadyExists.class)
  public ResponseEntity<ApiErrorResponse> handleTableAlreadyExists(TableAlreadyExists ex) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Já existe uma mesa com o código \"" + ex.getMessage() + "\".",
            null);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(
      org.springframework.web.context.request.async.AsyncRequestNotUsableException.class)
  public ResponseEntity<Void> handleAsyncRequestNotUsableException(
      org.springframework.web.context.request.async.AsyncRequestNotUsableException ex) {
    log.debug("Client disconnected during async request (SSE): {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
    log.error("Unhandled exception in API request: ", ex);
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro interno no servidor.",
            null);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
