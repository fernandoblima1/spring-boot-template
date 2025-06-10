package com.heynet.spring_template.models.dtos.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ResponseModel<T> {

  private Boolean success;

  private String message;

  @Builder.Default private T data = null;

  private Long totalElements;
  private Integer totalPages;

  @Singular("item")
  private List<T> list;

  @Singular("error")
  private Map<Object, Object> errors;

  private UUID traceId;
}
