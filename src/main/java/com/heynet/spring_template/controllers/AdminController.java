package com.heynet.spring_template.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heynet.spring_template.config.annotations.HasPermission;
import com.heynet.spring_template.config.annotations.HasRole;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Endpoints para administração")
public class AdminController {

  @GetMapping
  @HasRole("ADMIN")
  public String getAdmin() {
    return "Admin";
  }

  @GetMapping("/user")
  @HasPermission("user:read")
  public String getUser() {
    return "User";
  }

  @GetMapping("/user/{id}")
  public String getUserById(@PathVariable Long id) {
    return "User " + id;
  }
}
