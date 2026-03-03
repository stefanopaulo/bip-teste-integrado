package com.example.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record TransferenciaRequest(
		@NotNull(message = "É necessário informar o emissor")
		Long fromId,
		
		@NotNull(message = "É necessario informar o destinatário")
		Long toId,
		
		@NotNull(message = "É necessário informar o valor a ser enviado")
		BigDecimal amount
		) {

}
