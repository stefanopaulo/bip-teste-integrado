package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BeneficioUpdateRequest(

		@NotBlank(message = "O nome é obrigátorio")
		String nome,

		@Size(min = 1, message = "A descrição, se informada, não pode estar vazia")
		String descricao

) {

}
