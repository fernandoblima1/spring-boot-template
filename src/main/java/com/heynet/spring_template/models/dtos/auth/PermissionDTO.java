package com.heynet.spring_template.models.dtos.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionDTO {
  private String module;
  private String action;
}
