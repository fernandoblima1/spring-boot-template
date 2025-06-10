package com.heynet.spring_template.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heynet.spring_template.models.entities.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {}
