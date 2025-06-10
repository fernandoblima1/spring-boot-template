package com.heynet.spring_template.models.enums;

public enum Action {
  all("all"),
  read("read"),
  write("write"),
  delete("delete"),
  update("update");

  private String codigo;

  Action(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public static Action fromCodigo(String codigo) {
    for (Action action : Action.values()) {
      if (action.getCodigo().equals(codigo)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Código de Ação inválido: " + codigo);
  }
}
