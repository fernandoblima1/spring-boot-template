package com.heynet.spring_template.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heynet.spring_template.config.annotations.HasPermission;
import com.heynet.spring_template.models.dtos.auth.AuthRequestDTO;
import com.heynet.spring_template.models.dtos.auth.AuthResponseDTO;
import com.heynet.spring_template.models.dtos.auth.ForgotPasswordRequestDTO;
import com.heynet.spring_template.models.dtos.auth.LoginRequestDTO;
import com.heynet.spring_template.models.dtos.auth.RegisterPermissionDTO;
import com.heynet.spring_template.models.dtos.auth.ResetPasswordRequestDTO;
import com.heynet.spring_template.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints para autenticação e autorização")
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "Login",
      description = "Login de usuário",
      security = @SecurityRequirement(name = "none"))
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @Operation(
      summary = "Refresh Token",
      description = "Refresh Token",
      security = @SecurityRequirement(name = "none"))
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(authService.refreshToken(token));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
    log.info("Register: {}", request);
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/assign-permissions/{username}")
  @HasPermission("user:manage_permissions")
  public ResponseEntity<String> assignPermissions(
      @PathVariable String username, @Valid @RequestBody RegisterPermissionDTO permissionData) {
    try {
      authService.assignPermissions(username, permissionData);
      return ResponseEntity.ok("Permissões atribuídas com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/remove-permissions/{username}")
  @HasPermission("user:manage_permissions")
  public ResponseEntity<String> removePermissions(
      @PathVariable String username, @Valid @RequestBody RegisterPermissionDTO permissionData) {
    try {
      authService.removePermissions(username, permissionData);
      return ResponseEntity.ok("Permissões removidas com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequestDTO request) {
    try {
      authService.requestPasswordReset(request);
      return ResponseEntity.ok("Email de reset de senha enviado com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
    try {
      authService.resetPassword(request);
      return ResponseEntity.ok("Senha alterada com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
