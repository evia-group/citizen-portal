package com.evia.portal.serviceportal.core.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorCodes {

  private String httpCode;

  private String message;

  private Date timeStamp;

  private List<String> errors;

  private String details;
}
