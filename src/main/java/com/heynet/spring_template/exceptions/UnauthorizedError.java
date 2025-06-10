package com.heynet.spring_template.exceptions;

public class UnauthorizedError extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UnauthorizedError(String message) {
    super(message);
  }
}
