package com.heynet.spring_template.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heynet.spring_template.models.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByResetToken(String resetToken);

  List<User> findByRefreshTokenIsNotNull();

  Optional<User> findByEmail(String email);
}
