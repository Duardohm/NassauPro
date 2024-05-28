package com.nassaupro.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nassaupro.crud.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	boolean existsByName(String name);

}
