package com.evia.portal.adminportal.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

  private final List<String> errors;

  public EntityNotFoundException(final String message) {
    super(message);
    this.errors = null;
  }
}
