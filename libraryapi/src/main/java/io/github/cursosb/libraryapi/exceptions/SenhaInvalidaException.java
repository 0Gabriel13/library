package io.github.cursosb.libraryapi.exceptions;

public class SenhaInvalidaException extends RuntimeException {
	public SenhaInvalidaException() {
		super("Senha inválida!");
	}
}
