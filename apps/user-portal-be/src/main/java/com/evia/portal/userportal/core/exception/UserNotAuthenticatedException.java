package com.evia.portal.userportal.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class UserNotAuthenticatedException extends RuntimeException {

  public static final String NOT_AUTHENTICATED_MESSAGE = "User not authenticated";

  private final List<String> errors;

  public UserNotAuthenticatedException() {
    super(NOT_AUTHENTICATED_MESSAGE);
    this.errors = null;
  }
}
