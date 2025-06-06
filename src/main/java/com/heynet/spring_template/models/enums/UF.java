package com.heynet.spring_template.models.enums;

// Modelo de enumeração para as Unidades Federativas do Brasil
// Chave: sigla da UF
// Valor: UF em string

public enum UF {
  AC("AC"),
  AL("AL"),
  AM("AM"),
  AP("AP"),
  BA("BA"),
  CE("CE"),
  DF("DF"),
  ES("ES"),
  GO("GO"),
  MA("MA"),
  MG("MG"),
  MS("MS"),
  MT("MT"),
  PA("PA"),
  PB("PB"),
  PE("PE"),
  PI("PI"),
  PR("PR"),
  RJ("RJ"),
  RN("RN"),
  RO("RO"),
  RR("RR"),
  RS("RS"),
  SC("SC"),
  SE("SE"),
  SP("SP"),
  TO("TO");

  private String codigo;

  UF(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public static UF fromCodigo(String codigo) {
    for (UF uf : UF.values()) {
      if (uf.getCodigo().equals(codigo)) {
        return uf;
      }
    }
    throw new IllegalArgumentException("Código de UF inválido: " + codigo);
  }
}
