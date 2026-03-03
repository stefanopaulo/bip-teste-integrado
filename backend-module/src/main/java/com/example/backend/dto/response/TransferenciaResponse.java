package com.example.backend.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TransferenciaResponse(
		String mensagem,
		
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
		LocalDateTime dataHora
		) {

}
