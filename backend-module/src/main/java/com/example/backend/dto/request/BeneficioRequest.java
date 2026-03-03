package com.example.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BeneficioRequest(

		@NotBlank(message = "O nome é obrigátorio")
		String nome,

		@Size(min = 1, message = "A descrição, se informada, não pode estar vazia")
		String descricao,

		@NotNull(message = "O valor é obrigátorio")
		@Positive(message = "O valor não pode ser negativo")
		BigDecimal valor

) {

}
