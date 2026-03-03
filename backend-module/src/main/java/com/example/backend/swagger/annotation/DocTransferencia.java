package com.example.backend.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.backend.dto.erro.ApiError;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
    @ApiResponse(responseCode = "400", description = """
        Erro de validação ou regra de negócio:
        - Ids não podem ser nulos
        - Ids devem ser diferentes
        - Valor deve ser maior que zero
        - Saldo insuficiente
        """, 
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "404", description = "Um ou ambos os beneficiários não foram encontrados ou estão inativos", 
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "409", description = "Conflito de concorrência: o saldo foi alterado por outra operação. Tente novamente.", 
        content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public @interface DocTransferencia { }