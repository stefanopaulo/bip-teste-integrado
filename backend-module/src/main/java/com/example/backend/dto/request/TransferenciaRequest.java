package com.example.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record TransferenciaRequest(
		@NotBlank(message = "É necessário informar o emissor")
		Long fromId,
		
		@NotBlank(message = "É necessario informar o destinatário")
		Long toId,
		
		@NotBlank(message = "É necessário informar o valor a ser enviado")
		BigDecimal amount
		) {

}
