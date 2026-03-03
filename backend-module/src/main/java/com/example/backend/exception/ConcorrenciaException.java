package com.example.backend.exception;

public class ConcorrenciaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ConcorrenciaException(String msg) {
		super(msg);
	}

}
