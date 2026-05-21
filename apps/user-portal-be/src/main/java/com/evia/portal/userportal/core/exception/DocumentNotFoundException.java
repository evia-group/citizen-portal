package com.evia.portal.userportal.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class DocumentNotFoundException extends RuntimeException {

  private final List<String> errors;

  public DocumentNotFoundException(final String message) {
    super(message);
    this.errors = new ArrayList<>();
  }
}
