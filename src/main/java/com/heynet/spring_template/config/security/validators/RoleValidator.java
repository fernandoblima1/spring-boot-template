package com.heynet.spring_template.config.security.validators;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.heynet.spring_template.config.annotations.HasRole;

@Component
public class RoleValidator {

  public boolean validate(HasRole hasRole) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;

    return authentication.getAuthorities().stream()
        .anyMatch(
            authority -> {
              String role = authority.getAuthority();
              for (String requiredRole : hasRole.value()) {
                if (role.equals(requiredRole)) {
                  return true;
                }
              }
              return false;
            });
  }
}
