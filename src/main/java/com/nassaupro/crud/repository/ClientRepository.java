package com.nassaupro.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nassaupro.crud.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

	boolean existsByEmail(String email);

	boolean existsByCpf(String cpf);
}
