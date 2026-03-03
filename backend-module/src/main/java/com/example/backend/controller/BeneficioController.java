package com.example.backend.controller;

import java.net.URI;
import java.time.LocalDateTime;

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

import com.example.backend.dto.request.BeneficioRequest;
import com.example.backend.dto.request.TransferenciaRequest;
import com.example.backend.dto.response.BeneficioResponse;
import com.example.backend.dto.response.TransferenciaResponse;
import com.example.backend.service.BeneficioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {
	
	private final BeneficioService beneficioService;

	public BeneficioController(BeneficioService beneficioService) {
		this.beneficioService = beneficioService;
	}
	
	@PostMapping
	public ResponseEntity<BeneficioResponse> inserir(@Valid @RequestBody BeneficioRequest request) {
		BeneficioResponse resp = beneficioService.inserir(request);
		
		URI uri = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(resp.id())
					.toUri();
		
		return ResponseEntity.created(uri).body(resp);
	}
	
	@GetMapping
	public ResponseEntity<Page<BeneficioResponse>> buscarBeneficiosAtivos(
			@PageableDefault(page = 0, size = 5, sort = "nome")
			Pageable pageable) {
		
		return ResponseEntity.ok().body(beneficioService.buscaTodosBeneficiosAtivos(pageable));
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<BeneficioResponse> buscaBeneficioAtivoPorId(@PathVariable Long id) {
		return ResponseEntity.ok().body(beneficioService.buscaBeneficioAtivoPorId(id));
		
	}
	
	@PutMapping("/{id}/atualizar")
	public ResponseEntity<BeneficioResponse> atualizar(@PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
		BeneficioResponse resp = beneficioService.atualizar(id, request);
		
		return ResponseEntity.ok().body(resp);
	}
	
	@DeleteMapping("/{id}/desativar")
	public ResponseEntity<Void> desativar(@PathVariable Long id) {
		beneficioService.desativar(id);
		
		return ResponseEntity.noContent().build();
	}
    
    @PostMapping("/transferir")
    public ResponseEntity<TransferenciaResponse> transferir(@RequestBody @Valid TransferenciaRequest request) {
    	beneficioService.transfer(request.fromId(), request.toId(), request.amount());
    	
    	TransferenciaResponse resp = new TransferenciaResponse("Transferência realizada com sucesso.", LocalDateTime.now());
    	
    	return ResponseEntity.ok().body(resp);
    }
}
