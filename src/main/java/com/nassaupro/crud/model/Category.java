package com.nassaupro.crud.model;


import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "CATEGORIES")
public class Category {

	@Schema(name = "id", example = "1")
    @Id
    @Column(name = "CATEGORY_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Schema(name = "name", example = "Fitness")
    @JsonProperty("name")
    @Column(name = "CATEGORY_NAME", columnDefinition = "VARCHAR(100)", nullable = false)
    @NotBlank(message = "O nome da categoria não pode estar nulo ou em branco")
    @Length(min = 3, max = 100, message = "O Nome da categoria deve conter entre {min} e {max} caracteres")
    private String name;
	
	@Schema(name = "description", example = "Categoria de serviços de condicionamento físico")
    @JsonProperty("description")
    @Column(name = "CATEGORY_DESCRIPTION", columnDefinition = "VARCHAR(500)", nullable = false)
    @Length(max = 500, message = "A descrição da categoria não pode conter mais de {max} caracteres")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Service> services;
}



