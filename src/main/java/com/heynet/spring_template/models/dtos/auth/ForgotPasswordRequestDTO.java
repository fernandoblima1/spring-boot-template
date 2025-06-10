package com.heynet.spring_template.models.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
  @NotBlank(message = "O email é obrigatório")
  @Email(message = "O email deve ser um endereço de email válido")
  private String email;
}
