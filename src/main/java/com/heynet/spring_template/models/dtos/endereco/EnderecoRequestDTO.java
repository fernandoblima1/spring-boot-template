package com.heynet.spring_template.models.dtos.endereco;

import com.heynet.spring_template.models.entities.Endereco;
import com.heynet.spring_template.models.enums.UF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data // @Getter e @Setter
@Builder // Método para simplificar a criação de objetos
// DTO para receber dados que serão usados para criar um novo Endereco
public class EnderecoRequestDTO {
  @NotBlank(message = "O CEP é obrigatório")
  @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos")
  private String cep;

  @NotBlank(message = "O logradouro é obrigatório")
  private String logradouro;

  @NotBlank(message = "O número é obrigatório")
  private String numero;

  private String complemento;

  @NotBlank(message = "O bairro é obrigatório")
  private String bairro;

  @NotBlank(message = "A cidade é obrigatória")
  private String cidade;

  @NotBlank(message = "O estado é obrigatório")
  private UF estado;

  private String referencia;

  public Endereco toEntity() {
    return Endereco.builder()
        .cep(cep)
        .logradouro(logradouro)
        .numero(numero)
        .complemento(complemento)
        .bairro(bairro)
        .cidade(cidade)
        .estado(estado)
        .referencia(referencia)
        .build();
  }
}
