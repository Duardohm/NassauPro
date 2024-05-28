package com.nassaupro.crud.model;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "SERVICES")
public class Service {

	@Schema(name = "id", example = "1")
    @Id
    @Column(name = "SERVICE_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(name = "name", example = "Serviço de Personal Trainer")
    @Column(name = "SERVICE_NAME", nullable = false)
    @NotBlank(message = "O nome do serviço não pode estar nulo ou em branco")
    @Length(min = 3, max = 100, message = "O Nome do serviço deve conter entre {min} e {max} caracteres")
    private String name;

    @Schema(name = "description", example = "Treinamento personalizado para condicionamento físico")
    @Column(name = "SERVICE_DESCRIPTION")
    @Length(max = 500, message = "A descrição do serviço não pode conter mais de {max} caracteres")
    private String description;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @Schema(name = "price", example = "25.0")
    @Column(name = "SERVICE_PRICE")
    @DecimalMin(value = "0.0", message = "O preço do serviço não pode ser negativo")
    private double price;

    @ManyToOne
    @JoinColumn(name = "CLIENT_ID", nullable = false)
    private Client client;
}
