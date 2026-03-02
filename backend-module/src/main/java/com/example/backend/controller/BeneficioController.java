package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.request.TransferenciaRequest;
import com.example.backend.service.BeneficioService;

import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {
	
	private final BeneficioService beneficioService;

	public BeneficioController(BeneficioService beneficioService) {
		this.beneficioService = beneficioService;
	}
	
    @GetMapping
    public List<String> list() {
        return Arrays.asList("Beneficio A", "Beneficio B");
    }
    
    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestBody @Valid TransferenciaRequest request) {
    	beneficioService.transfer(request.fromId(), request.toId(), request.amount());
    	return ResponseEntity.ok().body("Transferência realizada com sucesso");
    }
}
