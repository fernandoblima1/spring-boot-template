package com.heynet.spring_template.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.heynet.spring_template.exceptions.NotFoundError;
import com.heynet.spring_template.models.entities.User;
import com.heynet.spring_template.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String emailOrUsername) throws NotFoundError {
    User user =
        userRepository
            .findByEmail(emailOrUsername)
            .orElseGet(
                () ->
                    userRepository
                        .findByUsername(emailOrUsername)
                        .orElseThrow(() -> new NotFoundError("Usuário não encontrado")));

    Set<GrantedAuthority> authorities =
        user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> new SimpleGrantedAuthority(permission.toString()))
            .collect(Collectors.toSet());
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), authorities);
  }

  public UserDetails loadUserDetails(User user) {

    Set<GrantedAuthority> authorities =
        user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> new SimpleGrantedAuthority(permission.toString()))
            .collect(Collectors.toSet());
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), authorities);
  }
}
