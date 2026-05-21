package com.evia.portal.userportal.core.exception;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;
    private MethodParameter methodParameter;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/test");

        Method dummy = GlobalExceptionHandlerTest.class.getDeclaredMethod("setUp");
        methodParameter = new MethodParameter(dummy, -1);
    }

    // --- EntityNotFoundException ---

    @Test
    @DisplayName("should return 404 NOT_FOUND when EntityNotFoundException is thrown")
    void shouldReturn404WhenEntityNotFoundExceptionIsThrown() {
        EntityNotFoundException exception = new EntityNotFoundException(
            "User with id 1 was not found.");

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotFoundException(exception,
            webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User with id 1 was not found.");
        assertThat(response.getBody().getHttpCode()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(response.getBody().getDetails()).isEqualTo("uri=/api/v1/test");
        assertThat(response.getBody().getTimeStamp()).isNotNull();
    }

    @Test
    @DisplayName("should propagate errors list from EntityNotFoundException into the error response")
    void shouldPropagateErrorsListFromEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException(
            List.of("field must not be null", "id is invalid"));

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotFoundException(exception,
            webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).containsExactlyInAnyOrder(
            "field must not be null", "id is invalid");
    }

    @Test
    @DisplayName("should return null errors list when EntityNotFoundException has no error details")
    void shouldReturnNullErrorsListWhenEntityNotFoundExceptionHasNoDetails() {
        EntityNotFoundException exception = new EntityNotFoundException("Not found");

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotFoundException(exception,
            webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).isNull();
    }

    // --- EntityNotValidException ---

    @Test
    @DisplayName("should return 406 NOT_ACCEPTABLE when EntityNotValidException is thrown")
    void shouldReturn406WhenEntityNotValidExceptionIsThrown() {
        EntityNotValidException exception = new EntityNotValidException("Validation failed",
            List.of("name is blank"));

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotValidException(exception,
            webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getErrors()).containsExactly("name is blank");
    }

    @Test
    @DisplayName("should return empty errors list when EntityNotValidException is created with message only")
    void shouldReturnEmptyErrorsWhenEntityNotValidExceptionHasNoList() {
        EntityNotValidException exception = new EntityNotValidException("Invalid entity");

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotValidException(exception,
            webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).isEmpty();
    }

    // --- DocumentNotFoundException ---

    @Test
    @DisplayName("should return 406 NOT_ACCEPTABLE when DocumentNotFoundException is thrown")
    void shouldReturn406WhenDocumentNotFoundExceptionIsThrown() {
        DocumentNotFoundException exception = new DocumentNotFoundException(
            "No document was found under the given path");

        ResponseEntity<ErrorCodes> response = handler.handlerDocumentNotFoundException(exception,
            webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(
            "No document was found under the given path");
        assertThat(response.getBody().getErrors()).isNotNull();
    }

    @Test
    @DisplayName("should return 406 NOT_ACCEPTABLE with empty errors list for DocumentNotFoundException")
    void shouldReturnEmptyErrorsForDocumentNotFoundException() {
        DocumentNotFoundException exception = new DocumentNotFoundException("missing file");

        ResponseEntity<ErrorCodes> response = handler.handlerDocumentNotFoundException(exception,
            webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).isEmpty();
    }

    // --- Generic Exception handler ---

    @Test
    @DisplayName("should return 500 INTERNAL_SERVER_ERROR for an unhandled generic exception")
    void shouldReturn500ForUnhandledGenericException() {
        Exception exception = new RuntimeException("Something unexpected happened");

        ResponseEntity<ErrorCodes> response = handler.handlerGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Something unexpected happened");
        assertThat(response.getBody().getHttpCode()).isEqualTo(
            HttpStatus.INTERNAL_SERVER_ERROR.toString());
        assertThat(response.getBody().getErrors()).isNull();
    }

    @Test
    @DisplayName("should include request description in error response for generic exception")
    void shouldIncludeRequestDescriptionForGenericException() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/dogs");
        Exception exception = new IllegalStateException("bad state");

        ResponseEntity<ErrorCodes> response = handler.handlerGlobalException(exception, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetails()).isEqualTo("uri=/api/v1/dogs");
    }

    // --- MethodArgumentNotValidException handler ---

    @Test
    @DisplayName("should return field-level validation errors map for MethodArgumentNotValidException")
    void shouldReturnFieldValidationErrorsForMethodArgumentNotValidException() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "userDTO");
        bindingResult.addError(new FieldError("userDTO", "email", "must be a valid email"));
        bindingResult.addError(new FieldError("userDTO", "name", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter,
            bindingResult);

        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(ex, headers, status,
            webRequest);

        Assertions.assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertThat(errors).containsEntry("email", "must be a valid email");
        assertThat(errors).containsEntry("name", "must not be blank");
    }

    @Test
    @DisplayName("should return empty errors map when MethodArgumentNotValidException has no field errors")
    void shouldReturnEmptyErrorsMapWhenNoFieldErrors() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "userDTO");

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter,
            bindingResult);

        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.UNPROCESSABLE_ENTITY;

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(ex, headers, status,
            webRequest);

        Assertions.assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("should return single field error in validation response")
    void shouldReturnSingleFieldErrorInValidationResponse() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "profileDTO");
        bindingResult.addError(new FieldError("profileDTO", "dateOfBirth", "must not be null"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter,
            bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        Assertions.assertNotNull(response);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertThat(errors).hasSize(1).containsEntry("dateOfBirth", "must not be null");
    }
}
