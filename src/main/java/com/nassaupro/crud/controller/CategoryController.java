package com.nassaupro.crud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.nassaupro.crud.model.Category;
import com.nassaupro.crud.repository.CategoryRepository;

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
@RequestMapping("/categories")
@Tag(name = "Category Controller", description = "Mapeamento dos endpoints das categorias")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	// Endpoint para obter todas as categorias
	@Operation(summary = "Listar todos as categorias", description = "Retorna uma lista de todas as categorias.", tags = {
	"Get" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
				@Content(schema = @Schema(implementation = Category.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "204", description = "Não há categoria cadastrada", content = {
				@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao buscar os clientes", content = {
				@Content(schema = @Schema()) }) })
	@GetMapping("/list")
	public ResponseEntity<?> getAllCategories() {
		try {
			List<Category> categories = categoryRepository.findAll();

			if (!categories.isEmpty()) {
				return ResponseEntity.ok(categories); // Retorna 200 ok e lista de categorias se não estiver vazia
			} else {
				return ResponseEntity.status(HttpStatus.OK).body("Não há categoria cadastrada"); // Retorna 204 No Content se a lista estiver vazia
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 Internal Server Error em caso de erro
																					
		}
	}

	// Endpoint para obter uma categoria pelo ID
	@Operation(summary = "Listar a categoria por ID", description = "Retorna uma categoria específica.", tags = { "Get" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
				@Content(schema = @Schema(implementation = Category.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID: {id}", content = {
				@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", description = "Ocorreu um erro ao buscar o cliente pelo ID", content = {
				@Content(schema = @Schema()) }) })
	@Parameter(name = "id", description = "ID da categoria a ser listada", required = true, example = "1")
	@GetMapping("/list/{id}")
	public ResponseEntity<?> getCategoryById(@Valid @PathVariable Long id) {
		try {
			Optional<Category> category = categoryRepository.findById(id);

			if (category.isPresent()) {
				return ResponseEntity.ok(category.get()); // Retorna 200 Ok e a categoria encontrada
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado com o ID: " + id); // Retorna 404 Not Found se a categoria não for encontrada
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 Internal Server Error em caso de erro
																				
		}
	}

	// Endpoint para criar uma nova categoria
	@Parameters({
		@Parameter(name = "name", description = "Nome da categoria", required = true, example = "Fitness"),
		@Parameter(name = "description", description = "Descrição da categoria", required = true, example = "Categoria de serviços de condicionamento físico"), })
	@Operation(summary = "Cadastra uma nova categoria", description = "Cria uma nova categoria no banco de dados e retorna uma mensagem.", tags = {
		"Post" })
	@PostMapping("/create")
	public ResponseEntity<?> createCategory(@Valid @RequestBody Category newCategory, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			List<String> errorMessages = new ArrayList<>();

			for (ObjectError error : allErrors) {
				errorMessages.add(error.getDefaultMessage());
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
		}
		try {
			// Verifica se a categoria já existe pelo nome
			if (categoryRepository.existsByName(newCategory.getName())) {
				return ResponseEntity.badRequest().body("Já existe uma categoria com o mesmo nome"); // Retorna 400 Bad Request se a categoria já existir
																										
			}

			Category createdCategory = categoryRepository.save(newCategory);
			return ResponseEntity.ok(createdCategory); // Retorna 200 OK categoria criada com sucesso
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 Internal Server Error em caso de erro
																					
		}
	}

	// Endpoint para atualizar uma categoria existente pelo ID
	@PutMapping("/change/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
		try {
			Optional<Category> optionalCategory = categoryRepository.findById(id);

			if (optionalCategory.isPresent()) {
				Category existingCategory = optionalCategory.get();
				existingCategory.setName(updatedCategory.getName()); // Atualiza outros campos conforme necessário

				categoryRepository.save(existingCategory);
				return ResponseEntity.ok("Categoria atualizada com sucesso"); // Retorna a categoria atualizada com sucesso
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi encontrada uma categoria com o ID: " + id); // Retorna 404 Not Found se a categoria não for encontrada
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 Internal Server Error em caso de erro
																			
		}
	}

	// Endpoint para excluir uma categoria pelo ID
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		try {
			Optional<?> optionalCategory = categoryRepository.findById(id);

			if (optionalCategory.isPresent()) {
				categoryRepository.deleteById(id);
				return ResponseEntity.ok().body("Categoria deletada com sucesso"); // Retorna 200 Ok após a exclusão da categoria
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria não encontrada com o ID: " + id); // Retorna 404 Not Found se a categoria não for encontrada
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 Internal Server Error em caso de erro
																				
		}
	}
}
