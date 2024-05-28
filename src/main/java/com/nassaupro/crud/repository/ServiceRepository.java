package com.nassaupro.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nassaupro.crud.model.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {

}
