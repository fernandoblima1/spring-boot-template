package com.heynet.spring_template.exceptions;

public class BadRequestError extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BadRequestError(String message) {
    super(message);
  }
}
