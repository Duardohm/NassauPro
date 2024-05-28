package com.nassaupro.crud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nassaupro.crud.clientdto.ClientListDTO;
import com.nassaupro.crud.exception.ClientException;
import com.nassaupro.crud.model.Client;
import com.nassaupro.crud.repository.ClientRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Client Controller", description = "Mapeamento dos endpoints dos clientes")
@RequestMapping("/clients")
public class ClientController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ClientRepository clientRepository;

	// Endpoint para criar um novo cliente
	@Parameters({
			@Parameter(name = "firstname", description = "Primeiro nome do usuário", required = true, example = "Melo"),
			@Parameter(name = "lastname", description = "Sobrenome nome do usuário", required = true, example = "Meloso"),
			@Parameter(name = "email", description = "Email do usuário", required = true, example = "Melo.meloso@gmail.com"),
			@Parameter(name = "password", description = "Senha do usuário", required = true, example = "Melo123456"),
			@Parameter(name = "cpf", description = "CPF do usuário", required = false, example = "69475441069"),
			@Parameter(name = "phoneNumber", description = "Número de celular do usuário", required = true, example = "81912345678"),
			@Parameter(name = "userType", description = "Tipo do usuário", required = true, example = "CLIENT"), })
	@Operation(summary = "Cadastra um novo cliente", description = "Cria um novo cliente no banco de dados e retorna uma mensagem.", tags = {
			"Post" })
	@CrossOrigin(origins = "*")
	@PostMapping("/create")
	public ResponseEntity<?> createClient(@Valid @RequestBody Client client, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			List<String> errorMessages = new ArrayList<>();

			for (ObjectError error : allErrors) {
				errorMessages.add(error.getDefaultMessage());
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
		}

		try {
			// Verifica se o CPF já está em uso
			if (clientRepository.existsByCpf(client.getCpf())) {
				throw ClientException.cpfAlreadyExists(client.getCpf()); // Exceção para CPF já existente
			}
			// Salva o cliente no banco de dados
			clientRepository.save(client);
			return ResponseEntity.status(HttpStatus.CREATED).body("Cliente cadastrado com sucesso!"); // 201 Created

		} catch (ClientException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao criar o cliente"); // 500 Internal Server Error
		}
	}
	// -------------------------------------------------------------------------------

	// Endpoint para listar todos os clientes usando DTO
	@Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com nome, sobrenome e email de todos clientes cadastrados.", tags = {
			"Get" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = ClientListDTO.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "Não há cliente cadastrado", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao buscar os clientes", content = {
					@Content(schema = @Schema()) }) })
	@GetMapping("/list")
	public ResponseEntity<?> getAllClients() {
		try {
			List<Client> clients = clientRepository.findAll();

			if (clients.isEmpty()) {
				return ResponseEntity.ok("Não há cliente cadastrado"); // 200 OK
			}

			// Mapear Clientes para ClientDTOs
			List<ClientListDTO> clientListDTO = clients.stream()
					.map(client -> modelMapper.map(client, ClientListDTO.class)).collect(Collectors.toList());

			return ResponseEntity.ok(clientListDTO); // 200 OK e retorna o ClientListDTO
		} catch (Exception e) {
			// Tratando qualquer exceção inesperada (Ainda falta implementar)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro ao buscar os clientes");
		}
	}
	// -------------------------------------------------------------------------------

	// Endpoint para obter um cliente pelo ID
	@Operation(summary = "Listar o cliente por ID", description = "Retorna um cliente específico com nome, sobrenome e email.", tags = { "Get" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
				@Content(schema = @Schema(implementation = ClientListDTO.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID: {id}", content = {
				@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao buscar o cliente pelo ID", content = {
				@Content(schema = @Schema()) }) })
	@Parameter(name = "id", description = "ID do usuário a ser listado", required = true, example = "1")
	@CrossOrigin(origins = "*")
	@GetMapping("/list/{id}")
	public ResponseEntity<?> getClientById(@Valid @PathVariable Long id) {
		try {
			Optional<Client> optionalClient = clientRepository.findById(id);

			if (optionalClient.isPresent()) {
				Client client = optionalClient.get();

				ModelMapper modelMapper = new ModelMapper();
				ClientListDTO clientListDTO = modelMapper.map(client, ClientListDTO.class);

				return ResponseEntity.ok(clientListDTO); // 200 OK e retorna o DTO
			} else {
				throw ClientException.clientNotFound(id); // Exceção personalizada
			}
		} catch (ClientException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
					.body("Ocorreu um erro ao buscar o cliente pelo ID");
		}
	}
	// -------------------------------------------------------------------------------

	// Endpoint para atualizar um cliente existente
	@Operation(summary = "Atualizar um cliente por ID", description = "Atualizar cliente por ID informado.", tags = { "Put" })
	@Parameters({
		@Parameter(name = "firstname", description = "Primeiro nome do usuário", required = true, example = "Melo2"),
		@Parameter(name = "lastname", description = "Sobrenome nome do usuário", required = true, example = "Meloso2"),
		@Parameter(name = "email", description = "Email do usuário", required = true, example = "Melo.meloso2@gmail.com"),
		@Parameter(name = "password", description = "Senha do usuário", required = true, example = "Melo123456"),
		@Parameter(name = "cpf", description = "CPF do usuário", required = false, example = "69475441069"),
		@Parameter(name = "phoneNumber", description = "Número de celular do usuário", required = true, example = "81912345678"),
		@Parameter(name = "userType", description = "Tipo do usuário", required = true, example = "CLIENT"), })
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
				@Content(schema = @Schema(implementation = ClientListDTO.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID: {id}", content = {
				@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao buscar o cliente pelo ID", content = {
				@Content(schema = @Schema()) }) })
	@CrossOrigin(origins = "*")
	@PutMapping("/change/{id}")
	public ResponseEntity<?> updateClient(@PathVariable Long id, @Valid @RequestBody Client updatedClient,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			List<String> errorMessages = new ArrayList<>();

			for (ObjectError error : allErrors) {
				errorMessages.add(error.getDefaultMessage());
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
		}
		Optional<Client> optionalClient = clientRepository.findById(id);

		// Verificando se o cliente existe
		if (optionalClient.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado com o ID: " + id); // Cliente não encontrado - 404 Not Found
		}

		Client client = optionalClient.get();

		// Falta Jogar pra ClientException **

		// Validações e tratamento de erros
		if (updatedClient.getFirstName() != null) {
			client.setFirstName(updatedClient.getFirstName());
		}

		if (updatedClient.getLastName() != null) {
			client.setLastName(updatedClient.getLastName());
		}

		if (updatedClient.getEmail() != null && !updatedClient.getEmail().equals(client.getEmail())) {
			// Verifica se o email foi alterado
			if (isValidEmail(updatedClient.getEmail())) {
				client.setEmail(updatedClient.getEmail());
			} else {
				return ResponseEntity.badRequest().body("O campo 'email' não é um endereço de e-mail válido"); // 400 Request
			}
		}

		if (updatedClient.getPhoneNumber() != null) {
			client.setPhoneNumber(updatedClient.getPhoneNumber()); // Verificando o numero de telefone
		}

		// Salvar as alterações no banco de dados
		clientRepository.save(client);
		return ResponseEntity.ok("Dados atualizados com sucesso!"); // Retorna um código de status 200 OK
	}

	// Método de validação de e-mail
	private boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		return email.matches(emailRegex);
	}

	// -------------------------------------------------------------------------------

	// Endpoint para excluir um cliente pelo ID
	@Operation(summary = "Deletar cliente pelo ID", description = "Exclui um cliente pelo ID informado.", tags = {
			"Delete" })
	@Parameter(name = "id", description = "ID do usuário a ser excluido", required = true, example = "1")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID {id}", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao excluir o cliente", content = {
					@Content(schema = @Schema()) }) })
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteClient(@PathVariable Long id) {
		try {
			// Verifica se o cliente existe antes de excluir
			if (clientRepository.existsById(id)) {
				clientRepository.deleteById(id);
				return ResponseEntity.ok("Cliente deletado com sucesso"); // Exclusão bem-sucedida - 200 OK
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado com o ID: " + id); // 404 Not Found
			}
		} catch (Exception e) {
			// Tratando qualquer exceção inesperada (Falta Melhorar)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao excluir o cliente");
		}
	}
}
