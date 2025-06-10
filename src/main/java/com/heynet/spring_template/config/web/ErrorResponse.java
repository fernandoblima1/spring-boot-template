package com.heynet.spring_template.config.web;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private Map<String, String> fieldErrors;

  public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.message = message;
    this.fieldErrors = fieldErrors;
  }

  public ErrorResponse(int status, String error, String message) {
    this(status, error, message, null);
  }
}
