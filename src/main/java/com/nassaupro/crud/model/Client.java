package com.nassaupro.crud.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "CLIENTS")
public class Client {

	@Schema(name = "id", example = "1")
	@Id
	@Column(name = "CLIENT_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<Service> services;

	@Schema(name = "firstName", example = "Melo")
	@Column(name = "F_NAME", columnDefinition = "VARCHAR(40)", nullable = false)
	@NotBlank(message = "O nome não pode estar nulo ou em branco")
	@Length(min = 3, max = 40, message = "O Nome deve conter entre {min} e {max} caracteres")
	@Pattern(regexp = "^[a-zA-ZúÚíÍóÓýÝéÉáÁçÇãÃõÕôÔêÊûÛ\\s]+$", message = "O nome só deve conter letras")
	private String firstName;
	
	@Schema(name = "lastName", example = "Meloso")
	@Column(name = "L_NAME", columnDefinition = "VARCHAR(40)", nullable = false)
	@NotBlank(message = "O nome não pode estar nulo ou em branco")
	@Length(min = 3, max = 40, message = "O sobrenome deve conter entre {min} e {max} caracteres")
	@Pattern(regexp = "^[a-zA-ZúÚíÍóÓýÝéÉáÁçÇãÃõÕôÔêÊûÛ\\s]+$", message = "O sobrenome só deve conter letras")
	private String lastName;

	@Schema(name = "email", example = "melo.meloso@gmail.com")
	@Email
	@NotBlank(message = "O Email não pode estar nulo ou em branco")
	@Column(name = "EMAIL", nullable = false)
	private String email;
	
	@Schema(name = "password", example = "Melo123456")
	@Length(min = 6, message = "A senha deve conter no mínimo {min} caracteres")
	@Column(name = "PASSWORD", nullable = false)
	private String password;
	
	@Schema(name = "cpf", example = "69475441069")
	@CPF(message = "O CPF informado não é válido")
	@Column(name = "CPF", columnDefinition = "VARCHAR(11)", unique = true)
	@Pattern(regexp = "^[0-9]+$", message = "O CPF deve ser composto apenas por números, sem pontos ou espaços em branco")
	private String cpf;

	
	@Schema(name = "phoneNumber", example = "81912345678")
	@NotBlank(message = "O celular não pode estar nulo ou em branco")
	@Column(name = "PHONE_NUMBER", nullable = false)
	@Pattern(regexp = "^[0-9]+$", message = "O celular só deve conter números")
	@Length(min = 11, max = 11, message = "O celular deve conter 11 números")
	private String phoneNumber;


	public enum UserType {
		STUDENT_PROVIDER, // Aluno prestador de serviços
		CLIENT // Cliente
	}
	
	@Schema(name = "userType", example = "CLIENT")
	@Enumerated(EnumType.STRING)
	@Column(name = "USER_TYPE", nullable = false)
	private UserType userType;
	
	
}
