package com.heynet.spring_template.models.dtos.auth;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterPermissionDTO {
  private String roleName;
  private Set<PermissionDTO> deniedPermissions;
  private Set<PermissionDTO> additionalPermissions;
}
