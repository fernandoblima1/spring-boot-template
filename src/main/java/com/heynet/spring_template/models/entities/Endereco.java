package com.heynet.spring_template.models.entities;

import java.util.UUID;

import com.heynet.spring_template.models.dtos.endereco.EnderecoDTO;
import com.heynet.spring_template.models.enums.UF;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enderecos")
@EqualsAndHashCode(callSuper = true)
public class Endereco extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "cep")
  private String cep;

  @Column(name = "logradouro")
  private String logradouro;

  @Column(name = "numero")
  private String numero;

  @Column(name = "complemento")
  private String complemento;

  @Column(name = "bairro")
  private String bairro;

  @Column(name = "cidade")
  private String cidade;

  @Enumerated(EnumType.STRING) // EnumType.STRING para armazenar como string e não como inteiro
  @Column(name = "estado")
  private UF estado;

  @Column(name = "referencia")
  private String referencia;

  public EnderecoDTO toDTO() { // Método para converter o modelo Endereco em um DTO de apresentação
    return EnderecoDTO.builder()
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
