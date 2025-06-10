package com.heynet.spring_template.models.entities;

import com.heynet.spring_template.models.dtos.auth.PermissionDTO;
import com.heynet.spring_template.models.enums.Action;

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
@Table(name = "permissions")
@EqualsAndHashCode(callSuper = true)
public class Permission extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "module")
  private String module;

  @Enumerated(EnumType.STRING)
  @Column(name = "action")
  private Action action;

  @Override
  public String toString() {
    return module + ":" + action;
  }

  public PermissionDTO toDTO() {
    return PermissionDTO.builder().module(module).action(action.toString()).build();
  }
}
