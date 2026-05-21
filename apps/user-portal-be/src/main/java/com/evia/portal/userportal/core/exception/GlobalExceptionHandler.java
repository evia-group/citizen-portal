package com.evia.portal.userportal.core.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorCodes> handlerEntityNotFoundException(EntityNotFoundException exception, WebRequest webRequest) {
    return createErrorResponse(
      HttpStatus.NOT_FOUND,
      exception,
      webRequest,
      exception.getErrors()
    );
  }

  @ExceptionHandler(EntityNotValidException.class)
  public ResponseEntity<ErrorCodes> handlerEntityNotValidException(EntityNotValidException exception, WebRequest webRequest) {
    return createErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception, webRequest, exception.getErrors());
  }

  @ExceptionHandler(DocumentNotFoundException.class)
  public ResponseEntity<ErrorCodes> handlerDocumentNotFoundException(DocumentNotFoundException exception, WebRequest webRequest) {
    return createErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception, webRequest, exception.getErrors());
  }

  // Default Exception (for ALL others)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorCodes> handlerGlobalException(Exception exception, WebRequest webRequest) {

    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, webRequest, null);
  }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex,
    @NonNull HttpHeaders headers,
    @NonNull HttpStatusCode status,
    @NonNull WebRequest request
  ) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(err -> {
      String fieldName = ((FieldError) err).getField();
      String message = err.getDefaultMessage();

      errors.put(fieldName, message);
    });

    return new ResponseEntity<>(errors, status);
  }

  private ResponseEntity<ErrorCodes> createErrorResponse(HttpStatus httpStatus, Exception exception, WebRequest webRequest, List<String> errors) {

    log.error(exception.getMessage(), exception);
    final ErrorCodes errorCodes = ErrorCodes.builder()
      .httpCode(httpStatus.toString())
      .message(exception.getMessage())
      .timeStamp(new Date())
      .errors(errors)
      .details(webRequest.getDescription(false))
      .build();
    return new ResponseEntity<>(errorCodes, httpStatus);
  }

}
