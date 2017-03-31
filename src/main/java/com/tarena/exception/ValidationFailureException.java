package com.tarena.exception;

public class ValidationFailureException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5924633231014667729L;
	private String message;

	public ValidationFailureException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
