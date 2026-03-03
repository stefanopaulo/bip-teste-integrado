package com.example.backend.controller.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.erro.ApiError;
import com.example.backend.exception.ConcorrenciaException;
import com.example.backend.exception.OperacaoIlegalException;
import com.example.backend.exception.RecursoNaoEncontradoException;
import com.example.backend.exception.SaldoInsuficienteException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ApiError> handleRecursoNaoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado.", e.getMessage(), request);
	}
	
	@ExceptionHandler(SaldoInsuficienteException.class)
	public ResponseEntity<ApiError> handleSaldoInsuficiente(SaldoInsuficienteException e, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Saldo insuficiente.", e.getMessage(), request);
	}
	
	@ExceptionHandler(OperacaoIlegalException.class)
	public ResponseEntity<ApiError> handleOperacaoIlegal(OperacaoIlegalException e, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Operação ilegal.", e.getMessage(), request);
	}
	
	@ExceptionHandler(ConcorrenciaException.class)
	public ResponseEntity<ApiError> handleConcorrencia(ConcorrenciaException e, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, "Erro na operação.", e.getMessage(), request);
	}
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        var error = "Dados inválidos.";
        var status = HttpStatus.BAD_REQUEST;
        
        List<String> erros = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        
        var err = new ApiError(Instant.now(), status.name(), error, erros, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    
    @ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGlobal(Exception e, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno.", "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.", request);
	}
    
	private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
	    var err = new ApiError(
	        Instant.now(), 
	        status.name(), 
	        error, 
	        List.of(message), 
	        request.getRequestURI()
	    );
	    return ResponseEntity.status(status).body(err);
	}
}
