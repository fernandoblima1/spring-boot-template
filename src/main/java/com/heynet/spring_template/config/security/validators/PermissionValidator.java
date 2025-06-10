package com.heynet.spring_template.config.security.validators;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.heynet.spring_template.config.annotations.HasPermission;

@Component
public class PermissionValidator {

  public boolean validate(HasPermission hasPermission) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;

    return authentication.getAuthorities().stream()
        .anyMatch(
            authority -> {
              String permission = authority.getAuthority();
              for (String requiredPermission : hasPermission.value()) {
                if (permission.equals(requiredPermission)) {
                  return true;
                }
              }
              return false;
            });
  }
}
