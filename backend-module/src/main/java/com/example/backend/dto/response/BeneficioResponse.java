package com.example.backend.dto.response;

import java.math.BigDecimal;

public record BeneficioResponse(
		
		Long id,
		
		String nome,

		String descricao,

		BigDecimal valor,
		
		Boolean ativo
		
		) {

}
