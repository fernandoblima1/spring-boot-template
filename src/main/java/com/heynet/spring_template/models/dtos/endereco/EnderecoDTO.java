package com.heynet.spring_template.models.dtos.endereco;

import com.heynet.spring_template.models.enums.UF;

import lombok.Builder;
import lombok.Data;

@Data // @Getter e @Setter
@Builder // Método para simplificar a criação de objetos
// DTO para representação do modelo Endereco
// Usado majoritariamente para a retorno de dados ao usuário

public class EnderecoDTO {
  private String cep;
  private String logradouro;
  private String numero;
  private String complemento;
  private String bairro;
  private String cidade;
  private UF estado;
  private String referencia;
}
