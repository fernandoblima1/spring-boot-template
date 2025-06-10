package com.heynet.spring_template.models.dtos.auth;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
  private String token;
  private String newPassword;
}
