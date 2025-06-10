package com.heynet.spring_template.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heynet.spring_template.models.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Optional<Permission> findByModuleAndAction(String module, String action);
}
