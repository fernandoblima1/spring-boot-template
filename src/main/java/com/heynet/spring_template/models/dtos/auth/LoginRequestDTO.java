package com.heynet.spring_template.models.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
  @NotBlank(message = "O email é obrigatório")
  @Email(message = "O email deve ser um email válido")
  private String email;

  @NotBlank(message = "A senha é obrigatória")
  @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres")
  private String password;
}
