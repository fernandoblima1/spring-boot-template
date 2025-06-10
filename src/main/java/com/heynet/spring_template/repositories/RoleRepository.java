package com.heynet.spring_template.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heynet.spring_template.models.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
