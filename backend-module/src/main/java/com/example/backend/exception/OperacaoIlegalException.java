package com.example.backend.exception;

public class OperacaoIlegalException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public OperacaoIlegalException(String msg) {
		super(msg);
	}

}
