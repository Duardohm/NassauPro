package com.nassaupro.crud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nassaupro.crud.clientdto.ServiceListDTO;
import com.nassaupro.crud.exception.ServiceException;
import com.nassaupro.crud.model.Category;
import com.nassaupro.crud.model.Client;
import com.nassaupro.crud.model.Service;
import com.nassaupro.crud.repository.CategoryRepository;
import com.nassaupro.crud.repository.ClientRepository;
import com.nassaupro.crud.repository.ServiceRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/services")
@Tag(name = "Service Controller", description = "Mapeamento dos endpoints dos serviços")
public class ServiceController {

	
	//teste para o ci/cd eduardo
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ClientRepository clientRepository;

	// Endpoint para criar um novo serviço
	@PostMapping("/create")
	public ResponseEntity<?> createService(@Valid @RequestBody Service service, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			List<String> errorMessages = new ArrayList<>();

			for (ObjectError error : allErrors) {
				errorMessages.add(error.getDefaultMessage());
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
		}
		
	    try {
	        if (service.getName() == null || service.getName().isEmpty()) {
	            return ResponseEntity.badRequest().body("O campo 'name' é obrigatório"); // 400 Bad Request
	        }
	        List<Category> categories = categoryRepository.findAll();
	        List<Client> clients = clientRepository.findAll();

			if (categories.isEmpty()) {
				return ResponseEntity.badRequest().body("Você não pode criar um serviço sem ter uma categoria para vincular");
			}else {
				if (clients.isEmpty()) {
				return ResponseEntity.badRequest().body("Você não pode criar um serviço sem ter um usuário para vincular");
				}else {
					serviceRepository.save(service);
			        return ResponseEntity.status(HttpStatus.CREATED).body("Serviço cadastrado com sucesso!"); // 201 Created
				}
			}
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao criar o serviço"); // 500 Internal Server Error
	    }
	}


	// Endpoint para listar todos os serviços
	@GetMapping("/list")
	public ResponseEntity<?> getAllServices() {
		try {
			List<Service> services = serviceRepository.findAll();

			if (services.isEmpty()) {
				return ResponseEntity.ok("Não há serviço cadastrado"); // 200 OK
			}
			
			List<ServiceListDTO> serviceListDTO = services.stream()
					.map(service -> modelMapper.map(service, ServiceListDTO.class)).collect(Collectors.toList());

			return ResponseEntity.ok(serviceListDTO); // 200 OK e retorna o ClientListDTO
		} catch (Exception e) {
			// Tratando qualquer exceção inesperada (Ainda falta implementar)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro ao buscar os clientes");
		}
	}


	// Endpoint para obter um serviço pelo ID
	@GetMapping("/list/{id}")
	public ResponseEntity<?> getServiceById(@Valid @PathVariable Long id) {
		try {
			Optional<Service> optionalService = serviceRepository.findById(id);

			if (optionalService.isPresent()) {
				Service service = optionalService.get();

				ModelMapper modelMapper = new ModelMapper();
				ServiceListDTO serviceListDTO = modelMapper.map(service, ServiceListDTO.class);

				return ResponseEntity.ok(serviceListDTO); // 200 OK e retorna o DTO
			} else {
				throw ServiceException.serviceNotFound(id); // Exceção personalizada
			}
		} catch (ServiceException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
					.body("Ocorreu um erro ao buscar o cliente pelo ID");
		}
	}

	// Endpoint para atualizar um serviço existente
	@PutMapping("/change/{id}")
	public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody Service updatedService,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			List<String> errorMessages = new ArrayList<>();

			for (ObjectError error : allErrors) {
				errorMessages.add(error.getDefaultMessage());
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
		}try {
			
	        Service service = serviceRepository.findById(id).orElse(null);

	        if (service != null) {
	            // Verifique e atualize os campos do serviço conforme necessário
	            if (updatedService.getName() != null && !updatedService.getName().isEmpty()) {
	                service.setName(updatedService.getName());
	            } else {
	                return ResponseEntity.badRequest().body("O campo nome não pode estar vazio"); // 400 Bad Request
	            }

	            // Salva as alterações no banco de dados
	            serviceRepository.save(service);

	            return ResponseEntity.ok("Serviço atualizado com sucesso"); // 200 OK
	        } else {
	        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado com o ID: " + id); // Serviço não encontrado - 404 Not Found
	        }
	    } catch (DataIntegrityViolationException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível atualizar o serviço devido a restrições de integridade de dados."); // 400 Bad Request
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao atualizar o serviço"); // 500 Internal Server Error
	    }
	}


	// Endpoint para excluir um serviço pelo ID
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteService(@PathVariable Long id) {
	    try {
	        if (serviceRepository.existsById(id)) {
	            serviceRepository.deleteById(id);
	            return ResponseEntity.ok("Serviço deletado com sucesso"); // Exclusão bem-sucedida - 200 OK
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado com o ID: " + id); // Serviço não encontrado - 404 Not Found
	        }
	    } catch (DataIntegrityViolationException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível excluir o serviço devido a dependências existentes."); // 400 Bad Request
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao excluir o serviço"); // 500 Internal Server Error
	    }
	}

}
