package com.heynet.spring_template.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.heynet.spring_template.exceptions.NotFoundError;
import com.heynet.spring_template.models.dtos.auth.AuthRequestDTO;
import com.heynet.spring_template.models.dtos.auth.AuthResponseDTO;
import com.heynet.spring_template.models.dtos.auth.ForgotPasswordRequestDTO;
import com.heynet.spring_template.models.dtos.auth.LoginRequestDTO;
import com.heynet.spring_template.models.dtos.auth.PermissionDTO;
import com.heynet.spring_template.models.dtos.auth.RegisterPermissionDTO;
import com.heynet.spring_template.models.dtos.auth.ResetPasswordRequestDTO;
import com.heynet.spring_template.models.entities.Permission;
import com.heynet.spring_template.models.entities.Role;
import com.heynet.spring_template.models.entities.User;
import com.heynet.spring_template.repositories.PermissionRepository;
import com.heynet.spring_template.repositories.RoleRepository;
import com.heynet.spring_template.repositories.UserRepository;
import com.heynet.spring_template.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final PermissionRepository permissionRepository;
  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;
  private final EmailService emailService;

  public AuthResponseDTO login(LoginRequestDTO request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundError("Usuário não encontrado com este email"));
    log.info("User found: {}", user);
    log.info("Request: {}", request);
    UserDetails userDetails = userDetailsService.loadUserDetails(user);
    Set<GrantedAuthority> authorities =
        user.getEffectivePermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.toString()))
            .collect(Collectors.toSet());
    List<String> roleNames =
        user.getRoles().stream().map(roleDTO -> roleDTO.getName()).collect(Collectors.toList());
    String accessToken = jwtUtil.generateToken(userDetails, authorities, roleNames);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

    // Salvar o refresh token no usuário
    user.setRefreshToken(refreshToken);
    userRepository.save(user);

    return AuthResponseDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .username(user.getUsername())
        .roles(user.getRoles())
        .build();
  }

  public AuthResponseDTO register(AuthRequestDTO request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("Usuário já existe com este email");
    }

    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new RuntimeException("Usuário já existe com este username");
    }

    // Buscar role padrão "USER"
    Role defaultRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new RuntimeException("Role USER não encontrada no sistema"));

    Set<Role> userRoles = new HashSet<>();
    userRoles.add(defaultRole);

    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(userRoles)
            .build();
    userRepository.save(user);

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

    Set<GrantedAuthority> authorities =
        user.getEffectivePermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.toString()))
            .collect(Collectors.toSet());

    List<String> roleNames =
        user.getRoles().stream().map(roleDTO -> roleDTO.getName()).collect(Collectors.toList());
    String accessToken = jwtUtil.generateToken(userDetails, authorities, roleNames);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

    // Salvar o refresh token no usuário
    user.setRefreshToken(refreshToken);
    userRepository.save(user);

    return AuthResponseDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .username(user.getUsername())
        .roles(user.getRoles())
        .build();
  }

  public AuthResponseDTO refreshToken(String refreshToken) {
    log.info("Refresh token: {}", refreshToken);
    refreshToken = jwtUtil.clearToken(refreshToken);
    String username = null;
    try {
      username = jwtUtil.extractUsername(refreshToken);
    } catch (Exception e) {
      throw new RuntimeException("Refresh token inválido");
    }

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundError("Usuário não encontrado"));
    log.info("User: {}", user);
    // Verificar se o refresh token enviado é o mesmo que está salvo no banco
    if (!refreshToken.equals(user.getRefreshToken())) {
      throw new RuntimeException("Refresh token inválido ou expirado");
    }

    // Verificar se o refresh token está expirado
    if (jwtUtil.isTokenExpired(refreshToken)) {
      // Limpar o refresh token expirado do banco
      user.setRefreshToken(null);
      userRepository.save(user);
      throw new RuntimeException("Refresh token expirado. Faça login novamente.");
    }

    // Validar o refresh token
    if (!jwtUtil.validateRefreshToken(refreshToken)) {
      // Limpar o refresh token inválido do banco
      user.setRefreshToken(null);
      userRepository.save(user);
      throw new RuntimeException("Refresh token inválido. Faça login novamente.");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    Set<GrantedAuthority> authorities =
        user.getEffectivePermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.toString()))
            .collect(Collectors.toSet());
    List<String> roleNames = jwtUtil.extractRoles(refreshToken);
    String newAccessToken = jwtUtil.generateToken(userDetails, authorities, roleNames);
    String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

    // Atualizar o refresh token no usuário
    user.setRefreshToken(newRefreshToken);
    userRepository.save(user);

    return AuthResponseDTO.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .username(user.getUsername())
        .roles(user.getRoles())
        .build();
  }

  public void logout(String refreshToken) {
    try {
      String username = jwtUtil.extractUsername(refreshToken);
      Optional<User> optionalUser = userRepository.findByUsername(username);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (refreshToken.equals(user.getRefreshToken())) {
          user.setRefreshToken(null);
          userRepository.save(user);
        }
      }
    } catch (Exception e) {
      // Token já inválido, não fazer nada
    }
  }

  public void changePassword(String username, String oldPassword, String newPassword) {
    User user = userRepository.findByUsername(username).orElseThrow();
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new RuntimeException("Senha antiga incorreta");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void cleanupExpiredRefreshTokens() {
    List<User> usersWithRefreshTokens = userRepository.findByRefreshTokenIsNotNull();
    for (User user : usersWithRefreshTokens) {
      try {
        if (user.getRefreshToken() != null && jwtUtil.isTokenExpired(user.getRefreshToken())) {
          user.setRefreshToken(null);
          userRepository.save(user);
        }
      } catch (Exception e) {

        user.setRefreshToken(null);
        userRepository.save(user);
      }
    }
  }

  public void requestPasswordReset(ForgotPasswordRequestDTO request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundError("Usuário não encontrado com este email"));

    // Gerar token seguro
    String resetToken = generateSecureToken();

    // Definir token e expiração (30 minutos)
    user.setResetToken(resetToken);
    user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));

    userRepository.save(user);

    // Enviar email
    emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
  }

  public void resetPassword(ResetPasswordRequestDTO request) {
    User user =
        userRepository
            .findByResetToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Token de reset inválido"));

    if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Token de reset expirado");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    user.setResetToken(null);
    user.setResetTokenExpiry(null);

    userRepository.save(user);
  }

  private String generateSecureToken() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    StringBuilder token = new StringBuilder();
    for (byte b : bytes) {
      token.append(String.format("%02x", b));
    }
    return token.toString();
  }

  public void assignPermissions(String username, RegisterPermissionDTO permissionData) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundError("Usuário não encontrado"));

    // Processar roles
    if (permissionData.getRoleName() != null) {
      Role role =
          roleRepository
              .findByName(permissionData.getRoleName())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          "Role " + permissionData.getRoleName() + " não encontrada"));
      user.getRoles().add(role.toDTO());
    }

    // Processar permissões adicionais
    if (permissionData.getAdditionalPermissions() != null) {
      for (PermissionDTO permission : permissionData.getAdditionalPermissions()) {
        Permission permissionEntity =
            permissionRepository
                .findByModuleAndAction(permission.getModule(), permission.getAction())
                .orElseThrow(
                    () -> new RuntimeException("Permissão " + permission + " não encontrada"));
        user.getAdditionalPermissions().add(permissionEntity);
      }
    }

    // Processar permissões negadas
    if (permissionData.getDeniedPermissions() != null) {
      for (PermissionDTO permission : permissionData.getDeniedPermissions()) {
        Permission permissionEntity =
            permissionRepository
                .findByModuleAndAction(permission.getModule(), permission.getAction())
                .orElseThrow(
                    () -> new RuntimeException("Permissão " + permission + " não encontrada"));
        user.getDeniedPermissions().add(permissionEntity);
      }
    }

    userRepository.save(user);
  }

  public void removePermissions(String username, RegisterPermissionDTO permissionData) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundError("Usuário não encontrado"));

    // Remover roles
    if (permissionData.getRoleName() != null) {
      Role role =
          roleRepository
              .findByName(permissionData.getRoleName())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          "Role " + permissionData.getRoleName() + " não encontrada"));
      user.getRoles().remove(role);
    }

    // Remover permissões adicionais
    if (permissionData.getAdditionalPermissions() != null) {
      for (PermissionDTO permission : permissionData.getAdditionalPermissions()) {
        Permission permissionEntity =
            permissionRepository
                .findByModuleAndAction(permission.getModule(), permission.getAction())
                .orElse(null);
        if (permissionEntity != null) {
          user.getAdditionalPermissions().remove(permissionEntity);
        }
      }
    }

    // Remover permissões negadas
    if (permissionData.getDeniedPermissions() != null) {
      for (PermissionDTO permission : permissionData.getDeniedPermissions()) {
        Permission permissionEntity =
            permissionRepository
                .findByModuleAndAction(permission.getModule(), permission.getAction())
                .orElse(null);
        if (permissionEntity != null) {
          user.getDeniedPermissions().remove(permissionEntity);
        }
      }
    }

    userRepository.save(user);
  }
}
