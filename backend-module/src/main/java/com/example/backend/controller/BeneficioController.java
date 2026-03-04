package com.example.backend.controller;

import java.net.URI;
import java.time.LocalDateTime;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.backend.dto.erro.ApiError;
import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.request.BeneficioUpdateRequest;
import com.example.backend.dto.request.TransferenciaRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.dto.response.TransferenciaResponse;
import com.example.backend.service.BeneficioService;
import com.example.backend.swagger.annotation.DocPadrao;
import com.example.backend.swagger.annotation.DocTransferencia;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Benefícios", description = "Endpoints para generenciamento de benefícios")
@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

	private final BeneficioService beneficioService;

	public BeneficioController(BeneficioService beneficioService) {
		this.beneficioService = beneficioService;
	}

	@Operation(summary = "Cadastrar benefício", description = "Cria um novo benefício no sistema com os dados fornecidos.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Benefício cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados enviados são inválidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	@PostMapping
	public ResponseEntity<BeneficioResponse> inserir(@Valid @RequestBody BeneficioRequest request) {
		BeneficioResponse resp = beneficioService.inserir(request);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.id()).toUri();

		return ResponseEntity.created(uri).body(resp);
	}

	@Operation(summary = "Listar benefícios ativos", description = "Retorna uma lista paginada de todos os benefícios que estão com status ativo.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	@GetMapping
	public ResponseEntity<Page<BeneficioResponse>> buscarBeneficiosAtivos(
			@ParameterObject @PageableDefault(page = 0, size = 5, sort = "nome") Pageable pageable) {

		return ResponseEntity.ok(beneficioService.buscaTodosBeneficiosAtivos(pageable));

	}

	@Operation(summary = "Consultar benefício por ID", description = "Busca os detalhes de um benefício específico. Retorna erro 404 caso não seja encontrado.")
	@DocPadrao
	@GetMapping("/{id}")
	public ResponseEntity<BeneficioResponse> buscaBeneficioAtivoPorId(@PathVariable Long id) {
		return ResponseEntity.ok(beneficioService.buscaBeneficioAtivoPorId(id));

	}

	@Operation(summary = "Atualizar benefício", description = "Altera os dados de um benefício existente e ativo.")
	@DocPadrao
	@PutMapping("/{id}")
	public ResponseEntity<BeneficioResponse> atualizar(@PathVariable Long id,
			@Valid @RequestBody BeneficioUpdateRequest request) {
		BeneficioResponse resp = beneficioService.atualizar(id, request);

		return ResponseEntity.ok(resp);
	}

	@Operation(summary = "Desativar benefício", description = "Altera o status do benefício para inativo (soft delete).")
	@DocPadrao
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> desativar(@PathVariable Long id) {
		beneficioService.desativar(id);

		return ResponseEntity.noContent().build();
	}

	@DocTransferencia
	@Operation(summary = "Transferir saldo entre benefícios", description = "Realiza a transferência de valores entre duas contas de benefício, validando regras de saldo e permissões.")
	@PostMapping("/transferir")
	public ResponseEntity<TransferenciaResponse> transferir(@RequestBody @Valid TransferenciaRequest request) {
		beneficioService.transfer(request.fromId(), request.toId(), request.amount());

		TransferenciaResponse resp = new TransferenciaResponse("Transferência realizada com sucesso.",
				LocalDateTime.now());

		return ResponseEntity.ok(resp);
	}
}
