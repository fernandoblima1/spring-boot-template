package com.heynet.spring_template.models.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.heynet.spring_template.models.dtos.auth.RoleDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "reset_token")
  private String resetToken;

  @Column(name = "reset_token_expiry")
  private java.time.LocalDateTime resetTokenExpiry;

  @Column(name = "refresh_token")
  private String refreshToken;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  // Permissões ADICIONAIS concedidas ao usuário (além das da role)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_additional_permissions",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  @Builder.Default
  private Set<Permission> additionalPermissions = new HashSet<>();

  // Permissões NEGADAS ao usuário (sobrepõem as da role)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_denied_permissions",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  @Builder.Default
  private Set<Permission> deniedPermissions = new HashSet<>();

  public Set<Permission> getEffectivePermissions() {
    Set<Permission> effective = new HashSet<>();

    // Adiciona todas as permissões das roles a qual o usuário pertence
    roles.forEach(role -> effective.addAll(role.getPermissions()));

    // Adiciona permissões adicionais
    effective.addAll(additionalPermissions);

    // Remove permissões negadas
    effective.removeAll(deniedPermissions);

    return effective;
  }

  public boolean hasPermission(Permission permission) {
    return getEffectivePermissions().contains(permission);
  }

  public boolean hasPermission(String module, String action) {
    return getEffectivePermissions().stream()
        .anyMatch(p -> p.getModule().equals(module) && p.getAction().toString().equals(action));
  }

  public Set<RoleDTO> getRoles() {
    return roles.stream().map(Role::toDTO).collect(Collectors.toSet());
  }
}
