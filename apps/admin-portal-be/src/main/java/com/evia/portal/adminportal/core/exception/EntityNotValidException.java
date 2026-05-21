package com.evia.portal.adminportal.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class EntityNotValidException extends RuntimeException {

  private final List<String> errors;

  public EntityNotValidException(final String message, final List<String> errors) {
    super(message);
    this.errors = errors;
  }
}
