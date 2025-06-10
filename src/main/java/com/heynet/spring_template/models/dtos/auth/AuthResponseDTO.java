package com.heynet.spring_template.models.dtos.auth;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
  private String username;
  private Set<RoleDTO> roles;
}
