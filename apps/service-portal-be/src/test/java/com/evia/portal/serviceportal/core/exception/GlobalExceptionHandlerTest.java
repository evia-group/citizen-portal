package com.evia.portal.serviceportal.core.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("handlerEntityNotFoundException_whenCalled_returns404WithErrorBody")
    void handlerEntityNotFoundException_whenCalled_returns404WithErrorBody() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotFoundException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Entity not found");
        assertThat(response.getBody().getHttpCode()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(response.getBody().getDetails()).isEqualTo("uri=/api/test");
        assertThat(response.getBody().getTimeStamp()).isNotNull();
        assertThat(response.getBody().getErrors()).isNull();
    }

    @Test
    @DisplayName("handlerEntityNotFoundException_whenExceptionHasErrorsList_includesErrorsInBody")
    void handlerEntityNotFoundException_whenExceptionHasErrorsList_includesErrorsInBody() {
        EntityNotFoundException exception = new EntityNotFoundException(List.of("field1 is invalid", "field2 is required"));

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotFoundException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).containsExactly("field1 is invalid", "field2 is required");
    }

    @Test
    @DisplayName("handlerEntityNotValidException_whenCalled_returns406WithErrorBody")
    void handlerEntityNotValidException_whenCalled_returns406WithErrorBody() {
        EntityNotValidException exception = new EntityNotValidException("Validation failed", List.of("name is blank"));

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotValidException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getHttpCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.toString());
        assertThat(response.getBody().getErrors()).containsExactly("name is blank");
    }

    @Test
    @DisplayName("handlerEntityNotValidException_whenNoErrors_returnsNullErrors")
    void handlerEntityNotValidException_whenNoErrors_returnsNullErrors() {
        EntityNotValidException exception = new EntityNotValidException("Simple validation failure");

        ResponseEntity<ErrorCodes> response = handler.handlerEntityNotValidException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).isNull();
    }

    @Test
    @DisplayName("handlerGlobalException_whenCalledWithGenericException_returns500WithErrorBody")
    void handlerGlobalException_whenCalledWithGenericException_returns500WithErrorBody() {
        Exception exception = new RuntimeException("Something went wrong");

        ResponseEntity<ErrorCodes> response = handler.handlerGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Something went wrong");
        assertThat(response.getBody().getHttpCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        assertThat(response.getBody().getDetails()).isEqualTo("uri=/api/test");
        assertThat(response.getBody().getTimeStamp()).isNotNull();
        assertThat(response.getBody().getErrors()).isNull();
    }

    @Test
    @DisplayName("handlerGlobalException_whenExceptionMessageIsNull_returnsNullMessageInBody")
    void handlerGlobalException_whenExceptionMessageIsNull_returnsNullMessageInBody() {
        Exception exception = new RuntimeException();

        ResponseEntity<ErrorCodes> response = handler.handlerGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isNull();
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid_whenBindingResultHasErrors_returnsMapOfFieldErrors")
    void handleMethodArgumentNotValid_whenBindingResultHasErrors_returnsMapOfFieldErrors() {
        FieldError fieldError1 = new FieldError("object", "name", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "email", "must be a valid email");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body).containsEntry("name", "must not be blank");
        assertThat(body).containsEntry("email", "must be a valid email");
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid_whenNoBindingErrors_returnsEmptyMap")
    void handleMethodArgumentNotValid_whenNoBindingErrors_returnsEmptyMap() {
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, webRequest
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body).isEmpty();
    }
}
