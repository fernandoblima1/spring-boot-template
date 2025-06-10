package com.heynet.spring_template.models.dtos.auth;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDTO {
  private Long id;
  private String name;
  private Set<PermissionDTO> permissions;
}
