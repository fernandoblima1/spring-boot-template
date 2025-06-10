package com.heynet.spring_template.models.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDTO {
  @NotBlank @Email private String username;

  @NotBlank
  @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres")
  private String password;

  @NotBlank
  @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres")
  private String confirmPassword;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres")
  private String confirmEmail;
}
