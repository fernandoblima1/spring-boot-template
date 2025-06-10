package com.heynet.spring_template.config.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.heynet.spring_template.exceptions.NotFoundError;
import com.heynet.spring_template.exceptions.UnauthorizedError;
import com.heynet.spring_template.models.dtos.response.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GenericExceptionHandler {
  @ExceptionHandler(InternalError.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseModel<Object> handleInternalError(InternalError e) {
    ResponseModel<Object> response = new ResponseModel<>();
    response.setMessage(e.getMessage());
    response.setSuccess(false);
    UUID traceId = UUID.randomUUID();
    response.setTraceId(traceId);
    log.error("[trace_id: " + traceId + "] - " + e.getMessage());

    return response;
  }

  @ExceptionHandler(UnauthorizedError.class)
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  public ResponseModel<Object> handleAppError(UnauthorizedError e) {
    ResponseModel<Object> response = new ResponseModel<>();
    response.setMessage(e.getMessage());
    response.setSuccess(false);
    UUID traceId = UUID.randomUUID();
    response.setTraceId(traceId);
    log.error("[trace_id: " + traceId + "] - " + e.getMessage());

    return response;
  }

  @ExceptionHandler(NotFoundError.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public ResponseModel<Object> handleAppError(NotFoundError e) {
    ResponseModel<Object> response = new ResponseModel<>();
    response.setMessage(e.getMessage());
    response.setSuccess(false);
    UUID traceId = UUID.randomUUID();
    response.setTraceId(traceId);
    log.error("[trace_id: " + traceId + "] - " + e.getMessage());

    return response;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<Object, Object> errors = new HashMap<>();
    UUID traceId = UUID.randomUUID();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      if ("typeMismatch".equals(error.getCode()) && error.getRejectedValue() != null) {
        errors.put(error.getField(), "Valor inválido");
      } else {
        errors.put(error.getField(), error.getDefaultMessage());
      }
    }

    ResponseModel<Object> response = new ResponseModel<>();
    response.setMessage("Um ou mais campos estão inválidos.");
    response.setTraceId(traceId);
    response.setErrors(errors);
    response.setSuccess(false);
    log.error("[trace_id: " + traceId + "] - " + ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
