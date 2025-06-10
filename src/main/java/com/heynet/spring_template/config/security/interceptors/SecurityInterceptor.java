package com.heynet.spring_template.config.security.interceptors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.heynet.spring_template.config.annotations.HasPermission;
import com.heynet.spring_template.config.annotations.HasRole;
import com.heynet.spring_template.config.security.validators.PermissionValidator;
import com.heynet.spring_template.config.security.validators.RoleValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor {

  private final RoleValidator roleValidator;
  private final PermissionValidator permissionValidator;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    HandlerMethod handlerMethod = (HandlerMethod) handler;

    HasRole methodRole = handlerMethod.getMethodAnnotation(HasRole.class);
    HasPermission methodPermission = handlerMethod.getMethodAnnotation(HasPermission.class);

    HasRole classRole = handlerMethod.getBeanType().getAnnotation(HasRole.class);
    HasPermission classPermission = handlerMethod.getBeanType().getAnnotation(HasPermission.class);

    if (methodRole != null && !roleValidator.validate(methodRole)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    }
    if (classRole != null && !roleValidator.validate(classRole)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    }

    if (methodPermission != null && !permissionValidator.validate(methodPermission)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    }
    if (classPermission != null && !permissionValidator.validate(classPermission)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    }

    return true;
  }
}
